
#include <lua.h>
#include <lauxlib.h>
#include <stdlib.h>
#include <inttypes.h>

const char * LONG_TYPE = "__long";
int64_t lua_checklong(lua_State *L, int index) {
  switch (lua_type(L, index)) {
    case LUA_TNUMBER:
      return (int64_t)lua_tonumber(L, index);
    case LUA_TSTRING:
      return atoll(lua_tostring(L, index));
    default:
      return *((int64_t *)luaL_checkudata(L, index, LONG_TYPE));
  }
}

// Creates a new long and pushes it onto the statck
int64_t * lua_pushlong(lua_State *L, int64_t *val) {
  int64_t *data = (int64_t *)lua_newuserdata(L, sizeof(int64_t)); 
  luaL_getmetatable(L, LONG_TYPE);                           
  lua_setmetatable(L, -2);                                     
  if (val) {
    *data = *val;
  }
  return data;
}
