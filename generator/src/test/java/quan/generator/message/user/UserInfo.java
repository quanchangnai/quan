package quan.generator.message.user;

import quan.network.message.Buffer;
import java.io.IOException;
import quan.network.message.Bean;

/**
 * Created by 自动生成
 */
public class UserInfo extends Bean {

    private String name;//名字
    private int level;//等级
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
        if (name == null){
            throw new NullPointerException();
        }
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
        if (eventState == null){
            throw new NullPointerException();
        }
        this.eventState = eventState;
    }

    public String getFunctionState() {
        return functionState;
    }

    public void setFunctionState(String functionState) {
        if (functionState == null){
            throw new NullPointerException();
        }
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
    public void encode(Buffer buffer) throws IOException {
        super.encode(buffer);
        buffer.writeString(name);
        buffer.writeInt(level);
        buffer.writeInt(experience);
        buffer.writeInt(icon);
        buffer.writeInt(power);
        buffer.writeInt(modifyNameCount);
        buffer.writeString(eventState);
        buffer.writeString(functionState);
        buffer.writeInt(lucky);
        buffer.writeInt(currentState);
        buffer.writeInt(buyPowerCount);
    }

    @Override
    public void decode(Buffer buffer) throws IOException {
        super.decode(buffer);
        name = buffer.readString();
        level = buffer.readInt();
        experience = buffer.readInt();
        icon = buffer.readInt();
        power = buffer.readInt();
        modifyNameCount = buffer.readInt();
        eventState = buffer.readString();
        functionState = buffer.readString();
        lucky = buffer.readInt();
        currentState = buffer.readInt();
        buyPowerCount = buffer.readInt();
    }

    @Override
    public String toString() {
        return "UserInfo{" +
                "name='" + name + '\'' +
                ",level=" + level +
                ",experience=" + experience +
                ",icon=" + icon +
                ",power=" + power +
                ",modifyNameCount=" + modifyNameCount +
                ",eventState='" + eventState + '\'' +
                ",functionState='" + functionState + '\'' +
                ",lucky=" + lucky +
                ",currentState=" + currentState +
                ",buyPowerCount=" + buyPowerCount +
                '}';

    }

}
