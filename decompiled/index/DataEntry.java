// Decompiled by DJ v3.11.11.95 Copyright 2009 Atanas Neshkov  Date: 5/8/2013 10:59:46 PM
// Home Page: http://members.fortunecity.com/neshkov/dj.html  http://www.neshkov.com/dj.html - Check often for new version!
// Decompiler options: packimports(3) 

package index;

import global.RID;
import global.SearchKey;

class DataEntry
{

    public DataEntry(SearchKey searchkey, RID rid1)
    {
        key = new SearchKey(searchkey);
        rid = new RID(rid1.pageno, rid1.slotno);
    }

    public DataEntry(byte abyte0[], short word0)
    {
        key = new SearchKey(abyte0, word0);
        rid = new RID(abyte0, (short)(word0 + key.getLength()));
    }

    public void writeData(byte abyte0[], short word0)
    {
        key.writeData(abyte0, word0);
        rid.writeData(abyte0, (short)(word0 + key.getLength()));
    }

    public short getLength()
    {
        return (short)(key.getLength() + rid.getLength());
    }

    public boolean equals(Object obj)
    {
        if(obj instanceof DataEntry)
        {
            DataEntry dataentry = (DataEntry)obj;
            return key.compareTo(dataentry.key) == 0 && rid.equals(dataentry.rid);
        } else
        {
            return false;
        }
    }

    public SearchKey key;
    public RID rid;
}
