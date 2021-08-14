
#include <lua.h>
#include <lauxlib.h>
#include <stdlib.h>
#include <math.h>
#include <inttypes.h>
#include <string.h>

extern const char * LONG_TYPE;
extern int64_t lua_checklong(lua_State *L, int index);
extern int64_t lua_pushlong(lua_State *L, int64_t *val);

////////////////////////////////////////////////////////////////////////////////

static void l_serialize(char *buf, int len, int64_t val) {
  snprintf(buf, len, "%"PRId64, val);
}

static int64_t l_deserialize(const char *buf) {
  int64_t data;
  int rv;
  // Support hex prefixed with '0x'
  if (strstr(buf, "0x") == buf) {
    rv = sscanf(buf, "%"PRIx64, &data);
  } else {
    rv = sscanf(buf, "%"PRId64, &data);
  }
  if (rv == 1) {
    return data;
  }
  return 0; // Failed
}

////////////////////////////////////////////////////////////////////////////////

static int l_new(lua_State *L) {
  int64_t val;
  const char *str = NULL;
  if (lua_type(L, 1) == LUA_TSTRING) {
    str = lua_tostring(L, 1);
    val = l_deserialize(str);
  } else if (lua_type(L, 1) == LUA_TNUMBER) {
    val = (int64_t)lua_tonumber(L, 1);
    str = (const char *)1;
  }
  lua_pushlong(L, (str ? val : NULL));
  return 1;
}

////////////////////////////////////////////////////////////////////////////////

// a + b
static int l_add(lua_State *L) {
  int64_t a, b, c;
  a = lua_checklong(L, 1);
  b = lua_checklong(L, 2);
  c = a + b;
  lua_pushlong(L, c);
  return 1;
}

// a / b
static int l_div(lua_State *L) {
  int64_t a, b, c;
  a = lua_checklong(L, 1);
  b = lua_checklong(L, 2);
  c = a / b;
  lua_pushlong(L, c);
  return 1;
}

// a == b
static int l_eq(lua_State *L) {
  int64_t a, b;
  a = lua_checklong(L, 1);
  b = lua_checklong(L, 2);
  lua_pushboolean(L, (a == b ? 1 : 0));
  return 1;
}

// garbage collection
static int l_gc(lua_State *L) {
  lua_pushnil(L);
  lua_setmetatable(L, 1);
  return 0;
}

// a < b
static int l_lt(lua_State *L) {
  int64_t a, b;
  a = lua_checklong(L, 1);
  b = lua_checklong(L, 2);
  lua_pushboolean(L, (a < b ? 1 : 0));
  return 1;
}

// a <= b
static int l_le(lua_State *L) {
  int64_t a, b;
  a = lua_checklong(L, 1);
  b = lua_checklong(L, 2);
  lua_pushboolean(L, (a <= b ? 1 : 0));
  return 1;
}

// a % b
static int l_mod(lua_State *L) {
  int64_t a, b, c;
  a = lua_checklong(L, 1);
  b = lua_checklong(L, 2);
  c = a % b;
  lua_pushlong(L, c);
  return 1;
}

// a * b
static int l_mul(lua_State *L) {
  int64_t a, b, c;
  a = lua_checklong(L, 1);
  b = lua_checklong(L, 2);
  c = a * b;
  lua_pushlong(L, c);
  return 1;
}

// a ^ b
static int l_pow(lua_State *L) {
  long double a, b;
  int64_t c;
  a = (long double)lua_checklong(L, 1);
  b = (long double)lua_checklong(L, 2);
  c = (int64_t)pow(a, b);
  lua_pushlong(L, c);
  return 1;
}

// a - b
static int l_sub(lua_State *L) {
  int64_t a, b, c;
  a = lua_checklong(L, 1);
  b = lua_checklong(L, 2);
  c = a - b;
  lua_pushlong(L, c);
  return 1;
}

// -a
static int l_unm(lua_State *L) {
  int64_t a, c;
  a = lua_checklong(L, 1);
  c = -a;
  lua_pushlong(L, c);
  return 1;
}

// tostring()
static int l_tostring(lua_State *L) {
  char str[256];
  l_serialize(str, 256, lua_checklong(L, 1));
  lua_pushstring(L, str);
  return 1;
}

// tonumber()
static int l_tonumber(lua_State *L)
{
  int64_t a = lua_checklong(L, 1);
  lua_pushnumber(L,(lua_Number)a);
  return 1;
}

////////////////////////////////////////////////////////////////////////////////

static const luaL_Reg mt_methods[] = {
  {"__add", l_add},
  {"__div", l_div},
  {"__eq", l_eq},
  {"__gc", l_gc},
  {"__lt", l_lt},
  {"__le", l_le},
  {"__mod", l_mod},
  {"__mul", l_mul},
  {"__pow", l_pow},
  {"__sub", l_sub},
  {"__unm", l_unm},
  {"__tostring", l_tostring},
  {NULL, NULL},
};

static const luaL_Reg funcs[] = {
  {"new", l_new},
  {NULL, NULL}
};

static const luaL_Reg index_methods[] = {
  {"tostring", l_tostring},
  {"tonumber", l_tonumber},
  {NULL, NULL}
};

////////////////////////////////////////////////////////////////////////////////

static void set_methods(lua_State *L,const struct luaL_Reg *methods) {
  // No need for a __index table since everything is __*
  for (; methods->name; methods++) {
    lua_pushstring(L, methods->name);    // mt, "name"
    lua_pushcfunction(L, methods->func); // mt, "name", func
    lua_rawset(L, -3);                   // mt
  }
}

static void create_cache(lua_State *L){
  lua_pushliteral(L,"cache");
  lua_newtable(L);

  luaL_newmetatable(L, "__long_cache");
  lua_pushliteral(L,"__mode");  // weak table
  lua_pushliteral(L,"kv");
  lua_rawset(L, -3);
  lua_setmetatable(L, -2);      

  lua_rawset(L, -3);
}

static void set_index_table(lua_State *L){
  lua_newtable(L);
  set_methods(L,index_methods);
  lua_setfield(L, -2, "__index");
}

LUALIB_API int luaopen_long(lua_State *L) {
  luaL_newmetatable(L, LONG_TYPE);
  set_methods(L, mt_methods);
  create_cache(L);
  set_index_table(L);

#if LUA_VERSION_NUM <= 501
  lua_newtable(L);
  luaL_register(L, NULL, funcs);
#elif
  luaL_newlib(L, funcs)
#endif

  lua_pushlong(L, LLONG_MIN);
  lua_setfield(L, -2, "min");
  lua_pushlong(L, LLONG_MAX);
  lua_setfield(L, -2, "max");
 
  return 1;
}