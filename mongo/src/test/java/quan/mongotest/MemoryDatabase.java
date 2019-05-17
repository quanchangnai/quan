package quan.mongotest;

import quan.mongo.Transactional;

import java.util.Arrays;
import java.util.List;

public class MemoryDatabase {

  @Transactional
  public void load(String info) {
    System.err.println("MemoryDatabase.load()");
//    return Arrays.asList(info + ": foo", info + ": bar");
  }

}