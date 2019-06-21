package quan.generator.message;

/**
 * Created by quanchangnai on 2017/7/6.
 */
public class MessageDefinition extends BeanDefinition {

    private String id;

    @Override
    public int getDefinitionType() {
        return TYPE_MESSAGE;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

}
