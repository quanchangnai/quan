---
--- Generated by EmmyLua(https://github.com/EmmyLua)
--- Created by quanchangnai.
--- DateTime: 2019/9/1 15:27
---

require("quan.table")

---
---基于VarInt和ZigZag编码的字节缓冲区，字节顺序采用小端模式
---
local Buffer = {
    ---类名
    class = "quan.message.Buffer"
}

function Buffer.new(bytes)
    assert(bytes == nil or type(bytes) == "string", "参数[bytes]类型错误")
    local instance = {
        ---字节缓冲区
        bytes = bytes or "",
        ---下一个读的位置,该位置的数据还未读
        readPos = 1,
    }
    setmetatable(instance, { __index = Buffer })
    return instance
end

function Buffer:size()
    return self.bytes:len()
end

function Buffer:reset()
    self.readPos = 1
end

function Buffer:clear()
    self.readPos = 1
    self.bytes = ""
end

function Buffer:readableCount()
    return self:size() - self.readPos + 1;
end

function Buffer:remainingBytes()
    return self.bytes:sub(self.readPos)
end

function Buffer:discardReadBytes()
    self.bytes = self.bytes:sub(self.readPos)
    self.readPos = 1;
end

---从buff重读取变长整数
---@param buffer
---@param bits 最多读几个bit位，合法值:16,32,64
local function readVarInt(buffer, bits)
    local shift = 0;
    local temp = 0;

    while shift < bits do
        if buffer.readPos > buffer:size() then
            error("读数据出错", 2)
        end

        local b = buffer.bytes:byte(buffer.readPos)
        buffer.readPos = buffer.readPos + 1

        temp = temp | (b & 0x7F) << shift;
        shift = shift + 7

        if (b & 0x80) == 0 then
            --ZigZag解码
            return (temp >> 1) ~ -(temp & 1);
        end
    end

    error("读数据出错", 2)
end

function Buffer:readBool()
    return self:readInt() ~= 0
end

function Buffer:readShort()
    return readVarInt(self, 16)
end

function Buffer:readInt()
    return readVarInt(self, 32)
end

function Buffer:readLong()
    return readVarInt(self, 64)
end

function Buffer:readFloat(scale)
    scale = scale or -1
    assert(math.type(scale) == "integer", "参数[scale]类型错误")

    if scale < 0 then
        if self.readPos + 3 > self:size() then
            error("读数据出错", 2)
        end
        local n = string.unpack("<f", self.bytes, self.readPos)
        self.readPos = self.readPos + 4
        return n
    end

    return self:readLong() / 10 ^ scale

end

function Buffer:readDouble(scale)
    scale = scale or -1
    assert(math.type(scale) == "integer", "参数[scale]类型错误")

    if scale < 0 then
        if self.readPos + 7 > self:size() then
            error("读数据出错", 2)
        end
        local n = string.unpack("<d", self.bytes, self.readPos)
        self.readPos = self.readPos + 8
        return n
    end

    return self:readLong() / 10 ^ scale
end

function Buffer:readBytes()
    local length = self:readInt()

    if self.readPos + length - 1 > self:size() then
        error("读数据出错", 2)
    end

    local bytes = self.bytes:sub(self.readPos, self.readPos + length - 1)
    self.readPos = self.readPos + length
    return bytes
end

function Buffer:readString()
    return self:readBytes()
end

local function writeVarInt(buffer, n, bits)
    assert(math.type(n) == "integer", "参数[n]类型错误")

    --ZigZag编码
    n = (n << 1) ~ (n >> 63);
    local shift = 0;

    while shift < bits do
        if ((n & ~0x7F) == 0) then
            buffer.bytes = buffer.bytes .. string.char(n & 0x7F)
            return
        else
            buffer.bytes = buffer.bytes .. string.char(n & 0x7F | 0x80)
            n = n >> 7
            shift = shift + 7
        end
    end

    error("写数据出错", 2)
end

function Buffer:writeBool(b)
    assert(type(b) == "boolean", "参数[b]类型错误")
    self:writeInt(b and 1 or 0)
end

function Buffer:writeShort(n)
    writeVarInt(self, n, 16)
end

function Buffer:writeInt(n)
    writeVarInt(self, n, 32)
end

function Buffer:writeLong(n)
    writeVarInt(self, n, 64)
end

function Buffer:writeFloat(n, scale)
    scale = scale or -1
    assert(type(n) == "number", "参数[n]类型错误")
    assert(math.type(scale) == "integer", "参数[scale]类型错误")

    if scale < 0 then
        self.bytes = self.bytes .. string.pack("<f", n)
    else
        self:writeDouble(n, scale)
    end
end

function Buffer:writeDouble(n, scale)
    scale = scale or -1
    assert(type(n) == "number", "参数[n]类型错误")
    assert(math.type(scale) == "integer", "参数[scale]类型错误")

    if scale < 0 then
        self.bytes = self.bytes .. string.pack("<d", n)
        return
    end

    local times = 10 ^ scale
    local threshold = 0x7fffffffffffffff / times;
    if n < -threshold or n > threshold then
        error(string.format("参数[%s]超出了限定范围[%s,%s],无法转换为指定精度[%s]的定点型数据", n, -threshold, threshold, scale), 2)
    else
        self:writeLong(math.floor(n * times))
    end
end

function Buffer:writeBytes(bytes)
    assert(type(bytes) == "string", "参数[bytes]类型错误")
    self:writeInt(bytes:len())
    self.bytes = self.bytes .. bytes
end

function Buffer:writeString(s)
    self:writeBytes(s)
end

Buffer = table.readOnly(Buffer)
return Buffer