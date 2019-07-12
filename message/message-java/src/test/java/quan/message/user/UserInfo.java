package quan.message.user;

import quan.message.Buffer;
import java.util.*;
import java.io.IOException;
import quan.message.Bean;

/**
 * Created by 自动生成
 */
public class UserInfo extends Bean {

    //名字
    private String name = "";

    //等级
    private int level;

    
    private int experience;

    
    private int icon;

    
    private int power;

    
    private int modifyNameCount;

    
    private String eventState = "";

    
    private String functionState = "";

    
    private int lucky;

    
    private int currentState;

    
    private int buyPowerCount;

    public UserInfo() {
    }

    public String getName() {
        return name;
    }

    public UserInfo setName(String name) {
        Objects.requireNonNull(name);
        this.name = name;
        return this;
    }

    public int getLevel() {
        return level;
    }

    public UserInfo setLevel(int level) {
        this.level = level;
        return this;
    }

    public int getExperience() {
        return experience;
    }

    public UserInfo setExperience(int experience) {
        this.experience = experience;
        return this;
    }

    public int getIcon() {
        return icon;
    }

    public UserInfo setIcon(int icon) {
        this.icon = icon;
        return this;
    }

    public int getPower() {
        return power;
    }

    public UserInfo setPower(int power) {
        this.power = power;
        return this;
    }

    public int getModifyNameCount() {
        return modifyNameCount;
    }

    public UserInfo setModifyNameCount(int modifyNameCount) {
        this.modifyNameCount = modifyNameCount;
        return this;
    }

    public String getEventState() {
        return eventState;
    }

    public UserInfo setEventState(String eventState) {
        Objects.requireNonNull(eventState);
        this.eventState = eventState;
        return this;
    }

    public String getFunctionState() {
        return functionState;
    }

    public UserInfo setFunctionState(String functionState) {
        Objects.requireNonNull(functionState);
        this.functionState = functionState;
        return this;
    }

    public int getLucky() {
        return lucky;
    }

    public UserInfo setLucky(int lucky) {
        this.lucky = lucky;
        return this;
    }

    public int getCurrentState() {
        return currentState;
    }

    public UserInfo setCurrentState(int currentState) {
        this.currentState = currentState;
        return this;
    }

    public int getBuyPowerCount() {
        return buyPowerCount;
    }

    public UserInfo setBuyPowerCount(int buyPowerCount) {
        this.buyPowerCount = buyPowerCount;
        return this;
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
