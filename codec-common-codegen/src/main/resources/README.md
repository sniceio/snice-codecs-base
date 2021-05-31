There is no way to figure out the MCC MNC by just parsing the value since the length of the MNC hasn't really been standardized. Therefore, Wireshark (and others I'm sure) are simply listing all known MCC/MNC combinations and use that to determine the correct length. Snice Codecs takes the same approach and is using all the good work the authors of Wireshark has done in regards to collecting all of this information from the ITU announcements.


