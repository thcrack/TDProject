class mapData{
  int mapID;
  mapData(int inputID){
    for(int i = 0; i < gridCount; i ++){
      routeGrid[i] = false;
    }
    mapID = inputID;
    switch(mapID){
      case 1:
        startpointX = -40;
        startpointY = 210;
        routeGrid[3] = true;
        routeGrid[13] = true;
        routeGrid[23] = true;
        routeGrid[33] = true;
        routeGrid[43] = true;
        routeGrid[42] = true;
        routeGrid[41] = true;
        routeGrid[51] = true;
        routeGrid[61] = true;
        routeGrid[62] = true;
        routeGrid[72] = true;
        routeGrid[82] = true;
        routeGrid[92] = true;
        routeGrid[93] = true;
        routeGrid[103] = true;
        routeGrid[113] = true;
        routeGrid[123] = true;
        routeGrid[122] = true;
        routeGrid[121] = true;
        routeGrid[141] = true;
        routeGrid[131] = true;
        routeGrid[151] = true;
        routeGrid[161] = true;
        routeGrid[162] = true;
        routeGrid[163] = true;
        routeGrid[173] = true;
        routeGrid[183] = true;
        routeGrid[184] = true;
        routeGrid[185] = true;
        routeGrid[175] = true;
        routeGrid[165] = true;
        routeGrid[166] = true;
        routeGrid[167] = true;
        routeGrid[157] = true;
        routeGrid[147] = true;
        routeGrid[137] = true;
        routeGrid[127] = true;
        routeGrid[126] = true;
        routeGrid[125] = true;
        routeGrid[115] = true;
        routeGrid[105] = true;
        routeGrid[95] = true;
        routeGrid[96] = true;
        routeGrid[97] = true;
        routeGrid[87] = true;
        routeGrid[77] = true;
        routeGrid[76] = true;
        routeGrid[66] = true;
        routeGrid[56] = true;
        routeGrid[57] = true;
        routeGrid[58] = true;
        routeGrid[68] = true;
        routeGrid[69] = true;
        lastGrid = 69;
        break;
    }
  }
}