package quan.protocol.user;

import java.io.IOException;
import quan.protocol.stream.WritableStream;
import quan.protocol.Bean;
import quan.protocol.stream.ReadableStream;

/**
 * Created by {@link quan.protocol.generator.JavaGenerator}
 */
public class UserInfo extends Bean {

    private String name;
    private int level;
    private int experience;
    private int icon;
    private int power;
    private int modifyNameCount;
    private String eventState;
    private String functionState;
    private int lucky;
    private int currentState;
    private int buyPowerCount;

    public UserInfo() {
        name = "";
        eventState = "";
        functionState = "";
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public int getExperience() {
        return experience;
    }

    public void setExperience(int experience) {
        this.experience = experience;
    }

    public int getIcon() {
        return icon;
    }

    public void setIcon(int icon) {
        this.icon = icon;
    }

    public int getPower() {
        return power;
    }

    public void setPower(int power) {
        this.power = power;
    }

    public int getModifyNameCount() {
        return modifyNameCount;
    }

    public void setModifyNameCount(int modifyNameCount) {
        this.modifyNameCount = modifyNameCount;
    }

    public String getEventState() {
        return eventState;
    }

    public void setEventState(String eventState) {
        this.eventState = eventState;
    }

    public String getFunctionState() {
        return functionState;
    }

    public void setFunctionState(String functionState) {
        this.functionState = functionState;
    }

    public int getLucky() {
        return lucky;
    }

    public void setLucky(int lucky) {
        this.lucky = lucky;
    }

    public int getCurrentState() {
        return currentState;
    }

    public void setCurrentState(int currentState) {
        this.currentState = currentState;
    }

    public int getBuyPowerCount() {
        return buyPowerCount;
    }

    public void setBuyPowerCount(int buyPowerCount) {
        this.buyPowerCount = buyPowerCount;
    }


    @Override
    public void serialize(WritableStream writable) throws IOException {
        writable.writeString(name);
        writable.writeInt(level);
        writable.writeInt(experience);
        writable.writeInt(icon);
        writable.writeInt(power);
        writable.writeInt(modifyNameCount);
        writable.writeString(eventState);
        writable.writeString(functionState);
        writable.writeInt(lucky);
        writable.writeInt(currentState);
        writable.writeInt(buyPowerCount);
    }

    @Override
    public void parse(ReadableStream readable) throws IOException {
        name = readable.readString();
        level = readable.readInt();
        experience = readable.readInt();
        icon = readable.readInt();
        power = readable.readInt();
        modifyNameCount = readable.readInt();
        eventState = readable.readString();
        functionState = readable.readString();
        lucky = readable.readInt();
        currentState = readable.readInt();
        buyPowerCount = readable.readInt();
    }

    @Override
    public String toString() {
        return "UserInfo{" +
                "name='" + name+ '\'' +
                ",level=" + level +
                ",experience=" + experience +
                ",icon=" + icon +
                ",power=" + power +
                ",modifyNameCount=" + modifyNameCount +
                ",eventState='" + eventState+ '\'' +
                ",functionState='" + functionState+ '\'' +
                ",lucky=" + lucky +
                ",currentState=" + currentState +
                ",buyPowerCount=" + buyPowerCount +
                '}';

    }

}
