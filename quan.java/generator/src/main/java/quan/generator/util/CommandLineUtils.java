package quan.generator.util;

import org.apache.commons.cli.*;

/**
 * Created by quanchangnai on 2019/8/29.
 */
public class CommandLineUtils {

    public static CommandLine parseCommandLine(String generatorName, String[] args, Option... extOptions) {
        Option definitionPathOption = new Option(null, "definitionPath" , true, "定义文件路径,多个路径以空格分隔" );
        definitionPathOption.setRequired(true);
        definitionPathOption.setArgs(Option.UNLIMITED_VALUES);

        Option codePathOption = new Option(null, "codePath" , true, "生成代码路径" );
        codePathOption.setRequired(true);

        Option packagePrefixOption = new Option(null, "packagePrefix" , true, "包名前缀" );
        packagePrefixOption.setRequired(true);
        Option enumPackagePrefixOption = new Option(null, "enumPackagePrefix" , true, "枚举包名前缀(可选)" );

        Options options = new Options();
        options.addOption(definitionPathOption);
        options.addOption(codePathOption);
        options.addOption(packagePrefixOption);
        options.addOption(enumPackagePrefixOption);
        for (Option extOption : extOptions) {
            options.addOption(extOption);
        }

        try {
            return new DefaultParser().parse(options, args);
        } catch (Exception e) {
            new HelpFormatter().printHelp(generatorName, options);
        }

        return null;
    }
}
