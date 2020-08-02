require("quan.table")

---
---任务类型
---代码自动生成，请勿手动修改
---
local QuestType = {
    --- 主线
    main = 1,
    --- 支线
    branch = 2
}

QuestType = table.readOnly(QuestType)
return QuestType