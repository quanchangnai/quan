package quan.generator.util;

import org.apache.commons.cli.*;

/**
 * Created by quanchangnai on 2019/8/29.
 */
public class CommandLineUtils {

    public static final String definitionPath = "definitionPath";

    public static final String codePath = "codePath";

    public static final String packagePrefix = "packagePrefix";

    public static final String enumPackagePrefix = "enumPackagePrefix";

    public static final String recalcId = "recalcId";

    private static CommandLineParser commandLineParser = new DefaultParser();

    private static HelpFormatter helpFormatter = new HelpFormatter();

    static {
        helpFormatter.setOptionComparator(null);
    }

    public static CommandLine parseCommandLine(String generatorName, String[] args, Option... extOptions) {
        Option definitionPathOption = new Option(null, CommandLineUtils.definitionPath, true, "定义文件路径,多个路径以空格分隔" );
        definitionPathOption.setRequired(true);
        definitionPathOption.setArgs(Option.UNLIMITED_VALUES);

        Option codePathOption = new Option(null, CommandLineUtils.codePath, true, "生成代码路径" );
        codePathOption.setRequired(true);

        Option packagePrefixOption = new Option(null, CommandLineUtils.packagePrefix, true, "包名前缀" );
        packagePrefixOption.setRequired(true);
        Option enumPackagePrefixOption = new Option(null, CommandLineUtils.enumPackagePrefix, true, "枚举包名前缀(可选)" );

        Options options = new Options();
        options.addOption(definitionPathOption);
        options.addOption(codePathOption);
        options.addOption(packagePrefixOption);
        options.addOption(enumPackagePrefixOption);
        for (Option extOption : extOptions) {
            options.addOption(extOption);
        }

        try {
            return commandLineParser.parse(options, args);
        } catch (Exception e) {
            helpFormatter.printHelp(generatorName, options);
        }

        return null;
    }

    public static CommandLine parseMessageCommandLine(String generatorName, String[] args) {
        Option recalcIdOption = new Option(null, CommandLineUtils.recalcId, false, "哈希计算消息ID冲突时是否重新计算(可选)" );
        return CommandLineUtils.parseCommandLine(generatorName, args, recalcIdOption);
    }
}
