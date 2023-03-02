require("quan.table")

---
---卡片类型-
---代码自动生成，请勿手动修改
---
local CardType = {
    --- 道具类型1
    type1 = 1,
    --- 道具类型2
    type2 = 2
}

CardType = table.readOnly(CardType)
return CardType