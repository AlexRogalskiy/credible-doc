package com.credibledoc.iso8583packer.navigator;

import com.credibledoc.iso8583packer.dump.Visualizer;
import com.credibledoc.iso8583packer.exception.PackerRuntimeException;
import com.credibledoc.iso8583packer.message.*;
import com.credibledoc.iso8583packer.tag.TagPacker;

import java.util.List;
import java.util.Objects;

/**
 * The service contains methods for navigation (jumping) inside the {@link MsgField}s graph.
 * 
 * @author Kyrylo Semenko
 */
public class NavigatorService implements Navigator {
    
    private static NavigatorService instance;
    
    protected Visualizer visualizer;

    /**
     * Static factory.
     * @return The single instance of the {@link NavigatorService}. 
     */
    public static NavigatorService getInstance() {
        if (instance == null) {
            instance = new NavigatorService();
        }
        return instance;
    }

    /**
     * Please use the {@link #getInstance()} method instead of this constructor.
     */
    public NavigatorService() {
        // empty
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T extends Msg> T findByName(List<? extends Msg> msgList, String name) {
        if (name == null) {
            return null;
        }
        if (msgList == null) {
            return null;
        }
        for (Msg nextMsgField : msgList) {
            if (name.equals(nextMsgField.getName())) {
                return (T) nextMsgField;
            }
        }
        return null;
    }

    @Override
    public MsgField getChildOrThrowException(String childName, MsgField currentMsgField) {
        List<MsgField> msgFields = currentMsgField.getChildren();
        MsgField child = findByName(msgFields, childName);
        if (child == null) {
            MsgField rootMsgField = findRoot(currentMsgField);
            String root = visualizer.dumpMsgField(rootMsgField);
            throw new PackerRuntimeException("Field with name '" + getPathRecursively(currentMsgField) +
                "' has no child with name '" + childName + "'. Current field: " + currentMsgField + "\n" +
                "Root MsgField:\n" + root);
        }
        return child;
    }

    @Override
    public String generatePath(Msg current) {
        if (current.getTagNum() != null) {
            if (current.getName() != null) {
                return current.getName() + "(" + current.getTagNum() + ")";
            }
            return String.valueOf(current.getTagNum());
        }
        if (current.getName() == null && current instanceof MsgField) {
            MsgField msgField = (MsgField) current;
            return msgField.getType().toString();
        }
        return current.getName();
    }

    /**
     * Call the {@link #generatePath(Msg)} method recursively.
     * @param current focused node in the object graph
     * @param prefix can be 'null'. If exists, a result will contain this prefix.
     * @return 'null' if name nor num has been set.
     */
    protected String generatePathRecursively(Msg current, String prefix) {
        if (prefix == null) {
            prefix = "";
        }
        if (current.getParent() != null) {
            prefix = generatePathRecursively(current.getParent(), prefix);
            prefix = prefix + "." + generatePath(current);
        } else {
            prefix = prefix + generatePath(current);
        }
        return prefix;
    }

    @Override
    public String getPathRecursively(Msg msg) {
        if (msg == null) {
            return null;
        }
        return generatePathRecursively(msg, null);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T extends Msg> T findRoot(T msg) {
        if (msg.getParent() == null) {
            return msg;
        }
        return (T)findRoot(msg.getParent());
    }

    @Override
    public TagPacker getTagPackerFromParent(MsgField msgField) {
        if (MsgFieldType.isNotTaggedType(msgField)) {
            return null;
        }
        if (msgField.getParent() != null) {
            return msgField.getParent().getChildrenTagPacker();
        }
        throw new PackerRuntimeException("This field '" + getPathRecursively(msgField) +
                "' has no parent. The parent is mandatory for obtaining of the ChildrenTagPacker property. " +
                "Please create a new Field and set it as a parent. Parent is not mandatory for fields which " +
                "contains the bitSet property.");
    }

    @Override
    public MsgField getSiblingOrThrowException(String siblingName, MsgField currentMsgField) {
        if (currentMsgField.getParent() == null) {
            throw new PackerRuntimeException("Field '" + getPathRecursively(currentMsgField) +
                    "' has no parent, hence it cannot have a sibling with name '" + siblingName + "'");
        }
        MsgField parentMsgField = currentMsgField.getParent();
        return getChildOrThrowException(siblingName, parentMsgField);
    }

    @Override
    public MsgField findByNameAndTagNumOrThrowException(MsgField msgField, MsgValue msgValue) {
        MsgField rootMsgField = findRoot(msgField);
        MsgField result = findInGraphRecurrently(msgValue, rootMsgField);
        if (result == null) {
            throw new PackerRuntimeException("Cannot find msgField for this field: " + getPathRecursively(msgValue));
        }
        return result;
    }

    protected MsgField findInGraphRecurrently(MsgValue msgValue, MsgField msgField) {
        if (isValueFitToField(msgValue, msgField)) {
            return msgField;
        }
        for (MsgField child : msgField.getChildren()) {
            MsgField nextResult = findInGraphRecurrently(msgValue, child);
            if (nextResult != null) {
                return nextResult;
            }
        }
        return null;
    }

    protected boolean isValueFitToField(MsgValue msgValue, MsgField msgField) {
        return Objects.equals(msgValue.getName(), msgField.getName()) &&
                Objects.equals(msgValue.getTagNum(), msgField.getTagNum());
    }

    @Override
    public MsgValue newFromNameAndTagNum(MsgField msgField) {
        MsgValue msgValue = new MsgValue();
        msgValue.setName(msgField.getName());
        msgValue.setTagNum(msgField.getTagNum());
        return msgValue;
    }

    @Override
    public void validateSameNamesAndTagNum(MsgPair msgPair) {
        MsgField msgField = msgPair.getMsgField();
        MsgValue msgValue = msgPair.getMsgValue();
        boolean namesEqual = Objects.equals(msgValue.getName(), msgField.getName());
        boolean tanNumsEqual = Objects.equals(msgValue.getTagNum(), msgField.getTagNum());
        if (!namesEqual || !tanNumsEqual) {
            String cause;
            if (!namesEqual && !tanNumsEqual) {
                cause = "names and tagNums";
            } else if (!namesEqual) {
                cause = "names";
            } else {
                cause = "tagNums";
            }
            throw new PackerRuntimeException("The MsgField and its msgValue does not fit to each other because " +
                    "they have different " + cause + ". MsgValue: '" + getPathRecursively(msgValue) +
                    "'. MsgField: '" + getPathRecursively(msgField) + "'. " +
                    "Please navigate to correct msgField. For this purpose use FieldBuilder.jump** methods.");
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T extends Msg> T findByTagNum(List<? extends Msg> msgList, Integer tagNum) {
        if (tagNum == null) {
            return null;
        }
        if (msgList == null) {
            return null;
        }
        for (Msg nextMsgField : msgList) {
            if (tagNum.equals(nextMsgField.getTagNum())) {
                return (T) nextMsgField;
            }
        }
        return null;
    }

    /**
     * @param visualizer see the {@link #visualizer} field description.
     */
    @Override
    public void setVisualizer(Visualizer visualizer) {
        this.visualizer = visualizer;
    }
}
