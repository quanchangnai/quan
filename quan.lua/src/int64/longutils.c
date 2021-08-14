
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

static int check_cache(lua_State *L,char* key){
  luaL_getmetatable(L, LONG_TYPE); 
  lua_pushliteral(L,"cache");
  lua_gettable(L,-2);

  lua_pushstring(L,key);
  lua_gettable(L,-2);
  
  int result = lua_isuserdata(L,-1);
  if (result == 1)
  {
    lua_remove(L,-2);
    lua_remove(L,-2);
  }else{
    lua_pop(L,3);
  }

  return result;
}

static void set_cache(lua_State *L,char* key){
  luaL_getmetatable(L, LONG_TYPE); 
  lua_pushliteral(L,"cache");
  lua_gettable(L,-2);

  lua_pushstring(L,key);
  lua_pushvalue(L,-4);
  lua_settable(L,-3);

  lua_pop(L,2);
}

// Creates a new long and pushes it onto the statck
void lua_pushlong(lua_State *L, int64_t val) {
  char key[256];
  snprintf(key, sizeof(key), "%"PRId64, val);
  if (check_cache(L,key)==1)
  {
    return;
  }
  
  int64_t *data = (int64_t *)lua_newuserdata(L, sizeof(int64_t)); 
  *data = val;
  luaL_getmetatable(L, LONG_TYPE);                           
  lua_setmetatable(L, -2);                                     

  set_cache(L,key);
}
