package quan.generator.util;

import org.apache.commons.cli.*;
import quan.config.TableType;

import java.util.Arrays;

/**
 * Created by quanchangnai on 2019/8/29.
 */
public class CommandLineUtils {

    public static final String definitionPath = "definitionPath";

    public static final String codePath = "codePath";

    public static final String packagePrefix = "packagePrefix";

    public static final String enumPackagePrefix = "enumPackagePrefix";

    // 消息ID冲突重新计算
    public static final String recalcId = "recalcId";

    //配置表类型
    public static final String tableType = "tableType";

    //配置表路径
    public static final String tablePath = "tablePath";


    private static CommandLineParser commandLineParser = new DefaultParser();

    private static HelpFormatter helpFormatter = new HelpFormatter();

    static {
        helpFormatter.setOptionComparator(null);
    }

    public static CommandLine parseArgs(String generatorName, String[] args, Option... extOptions) {
        Option definitionPathOption = new Option(null, CommandLineUtils.definitionPath, true, "定义文件路径,多个路径以空格分隔");
        definitionPathOption.setRequired(true);
        definitionPathOption.setArgs(Option.UNLIMITED_VALUES);

        Option codePathOption = new Option(null, CommandLineUtils.codePath, true, "生成代码路径");
        codePathOption.setRequired(true);

        Option packagePrefixOption = new Option(null, CommandLineUtils.packagePrefix, true, "包名前缀");
        packagePrefixOption.setRequired(true);
        Option enumPackagePrefixOption = new Option(null, CommandLineUtils.enumPackagePrefix, true, "枚举包名前缀(可选)");

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

    public static CommandLine parseMessageArgs(String generatorName, String[] args) {
        Option recalcIdOption = new Option(null, CommandLineUtils.recalcId, false, "哈希计算消息ID冲突时是否重新计算(可选)");
        return CommandLineUtils.parseArgs(generatorName, args, recalcIdOption);
    }

    public static CommandLine parseConfigArgs(String generatorName, String[] args) {
        Option tableTypeOption = new Option(null, CommandLineUtils.tableType, true, "配置表类型" + Arrays.toString(TableType.values()));
        tableTypeOption.setRequired(true);
        Option tablePathOption = new Option(null, CommandLineUtils.tablePath, true, "配置表路径");
        tablePathOption.setRequired(true);
        return CommandLineUtils.parseArgs(generatorName, args, tableTypeOption, tablePathOption);
    }
}
