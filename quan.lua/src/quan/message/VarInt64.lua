---
--- Generated by EmmyLua(https://github.com/EmmyLua)
--- Created by quanchangnai.
--- DateTime: 2021/8/5 18:27
---

local bits = require("quan.message.bits")

---@module VarInt64 64位变长整数
local VarInt64 = {}

---从buffer里读取变长整数
---@param buffer quan.message.Buffer
---@param maxBytes 最多读几个字节，short:3，int:5，long:10
function VarInt64.readVarInt(buffer, maxBytes)
    local temp = 0
    local shift = 0
    local count = 0

    while count < maxBytes do
        if buffer:readableCount() < 1 then
            break
        end

        local b = buffer:readByte()
        temp = temp | (b & 0x7F) << shift
        shift = shift + 7
        count = count + 1

        if (b & 0x80) == 0 then
            --ZigZag解码
            return (temp >> 1) ~ -(temp & 1)
        end
    end

    error("读数据出错")
end

---往buffer写入变长整数
---@param buffer quan.message.Buffer
---@param maxBytes 最多写几个字节，short:3，int:5，long:10
function VarInt64.writeVarInt(buffer, n, maxBytes)
    --assert(math.type(n) == "integer", "参数[n]类型错误")
    --ZigZag编码
    n = (n << 1) ~ bits.arshift(n, 63);

    local shift = 0
    local count = 0

    while count < maxBytes do
        if ((n & ~0x7F) == 0) then
            buffer:writeByte(n & 0x7F)
            return
        else
            buffer:writeByte(n & 0x7F | 0x80)
            n = n >> 7
            shift = shift + 7
            count = count + 1
        end
    end

    error("写数据出错")
end

return VarInt64