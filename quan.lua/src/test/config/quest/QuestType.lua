require("quan.table")

---
---任务类型
---自动生成
---
local QuestType = {
    --- 主线
    main = 1,
    --- 支线
    branch = 2
}

QuestType = table.readOnly(QuestType)
return QuestType