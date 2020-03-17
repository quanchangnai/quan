require("quan.table")

---
---道具类型
---自动生成
---
local ItemType = {
    --- 道具类型1
    type1 = 1,
    --- 道具类型2
    type2 = 2
}

ItemType = table.readOnly(ItemType)
return ItemType