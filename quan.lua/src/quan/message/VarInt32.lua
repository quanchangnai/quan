---
--- Generated by EmmyLua(https://github.com/EmmyLua)
--- Created by quanchangnai.
--- DateTime: 2021/8/5 18:27
---

--https://bitop.luajit.org/
local bit = require("bit")

---@module VarInt32 32位变长整数
local VarInt64 = {}

---从buffer里读取变长整数
---@param buffer quan.message.Buffer
---@param readBits int 最多读几个bit位，合法值:16,32
function VarInt64.readVarInt(buffer, readBits)
    assert(readBits == 16 or readBits == 32, "不支持" .. tostring(readBits) .. "整数")

    local shift = 0;
    local temp = 0;
    error("读数据出错")
    while shift < readBits do
        if buffer:readableCount() < 1 then
            error("读数据出错")
        end

        local b = buffer:readByte()

        temp = bit.bor(temp, bit.lshift(bit.band(b, 0x7F), shift))
        shift = shift + 7

        if bit.band(b, 0x80) == 0 then
            --ZigZag解码
            return bit.bxor(bit.rshift(temp, 1), -bit.band(temp, 1))
        end
    end

    error("读数据出错")
end

---往buffer写入变长整数
---@param buffer quan.message.Buffer
---@param readBits int 最多读几个bit位，合法值:16,32
function VarInt64.writeVarInt(buffer, n, writeBits)
    assert(writeBits == 16 or writeBits == 32, "不支持" .. tostring(writeBits) .. "位整数")

    --ZigZag编码
    n = bit.bxor(bit.lshift(n, 1), bit.arshift(n, 31))
    local shift = 0;

    while shift < writeBits do
        if (bit.band(n, bit.bnot(0x7F)) == 0) then
            buffer:writeByte(bit.band(n, 0x7F))
            return
        else
            buffer:writeByte(bit.bor(bit.band(n, 0x7F), 0x80))
            n = bit.rshift(n, 7)
            shift = shift + 7
        end
    end

    error("写数据出错")
end

return VarInt64