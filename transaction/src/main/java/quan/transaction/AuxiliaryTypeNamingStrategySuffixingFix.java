package quan.transaction;

import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.implementation.auxiliary.AuxiliaryType;

/**
 * Created by quanchangnai on 2019/5/19.
 */
public class AuxiliaryTypeNamingStrategySuffixingFix implements AuxiliaryType.NamingStrategy {

    private final String suffix;

    public AuxiliaryTypeNamingStrategySuffixingFix(String suffix) {
        this.suffix = suffix;
    }

    @Override
    public String name(TypeDescription instrumentedType) {
        return instrumentedType.getName() + "$" + suffix;
    }
}
