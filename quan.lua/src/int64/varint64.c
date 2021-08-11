
#include <lua.h>
#include <lauxlib.h>
#include <string.h>
#include <inttypes.h>

extern int64_t lua_checklong(lua_State *L, int index);
extern int64_t lua_pushlong(lua_State *L, int64_t *val);

/**
 * Convert an i64 to a varint. Results in 1-10 bytes on the buffer.
 */
static int encode(lua_State *L) {
  uint8_t data[10];
  int64_t p = lua_checklong(L, 1);
  uint32_t wsize = 0;
  luaL_Buffer buf;
  luaL_buffinit(L, &buf);

  // zigzag
  uint64_t n = (p << 1) ^ (p >> 63);

  while (1) {
    if ((n & ~0x7FL) == 0) {
      data[wsize++] = (int8_t)n;
      break;
    } else {
      data[wsize++] = (int8_t)((n & 0x7F) | 0x80);
      n >>= 7;
    }
  }

  luaL_addlstring(&buf, (void*)&data, wsize);
  luaL_pushresult(&buf);
  return 1;
}

/**
 * Convert a varint to i64. Convert one byte at a time.
 */
static int decode(lua_State *L) {
  int64_t result;
  uint8_t byte = luaL_checknumber(L, 1);
  int32_t shift = luaL_checknumber(L, 2);
  uint64_t n = (uint64_t)lua_checklong(L, 3);
  n |= (uint64_t)(byte & 0x7f) << shift;

  if (!(byte & 0x80)) {
    // zigzag
    result = (int64_t)(n >> 1) ^ (uint64_t)(-(int64_t)(n & 1));
    lua_pushnumber(L, 0);
  } else {
    result = n;
    lua_pushnumber(L, 1);
  }
  lua_pushlong(L, &result);
  return 2;
}

static const struct luaL_Reg lua_reg_varint64[] = {
  {"encode", encode},
  {"decode", decode},
  {NULL, NULL}
};

int luaopen_varint64(lua_State *L) {
  luaL_register(L, "varint64", lua_reg_varint64);
  return 1;
}