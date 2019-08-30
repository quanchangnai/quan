--
-- Created by IntelliJ IDEA.
-- User: quanchangnai
-- Date: 2019/8/30
-- Time: 17:04
-- To change this template use File | Settings | File Templates.
--
print(package.path)
local BuffTypeConfig = require("./BuffTypeConfig")

for k, v in pairs(BuffTypeConfig) do
    print("k:" .. k .. "，v:" .. tostring(v))
end

