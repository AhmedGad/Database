// Decompiled by DJ v3.11.11.95 Copyright 2009 Atanas Neshkov  Date: 5/8/2013 10:59:36 PM
// Home Page: http://members.fortunecity.com/neshkov/dj.html  http://www.neshkov.com/dj.html - Check often for new version!
// Decompiler options: packimports(3) 

package index;

import bufmgr.BufMgr;
import global.*;

// Referenced classes of package index:
//            SortedPage, DataEntry

class b extends SortedPage
{

    b()
    {
    }

    public int _mthif()
    {
        int i = getEntryCount();
        PageId pageid = getNextPage();
        b b1 = new b();
        PageId pageid1;
        for(; pageid.pid != -1; pageid = pageid1)
        {
            Minibase.BufferManager.pinPage(pageid, b1, false);
            i += b1.getEntryCount();
            pageid1 = b1.getNextPage();
            Minibase.BufferManager.unpinPage(pageid, false);
        }

        return i;
    }

    public boolean insertEntry(DataEntry dataentry)
    {
        try
        {
            super.insertEntry(dataentry);
        }
        catch(IllegalStateException illegalstateexception)
        {
            b b1 = new b();
            PageId pageid = getNextPage();
            if(pageid.pid != -1)
            {
                Minibase.BufferManager.pinPage(pageid, b1, false);
                boolean flag = b1.insertEntry(dataentry);
                Minibase.BufferManager.unpinPage(pageid, flag);
                return false;
            } else
            {
                PageId pageid1 = Minibase.BufferManager.newPage(b1, 1);
                setNextPage(pageid1);
                boolean flag1 = b1.insertEntry(dataentry);
                Minibase.BufferManager.unpinPage(pageid1, flag1);
                return true;
            }
        }
        return true;
    }

    public boolean deleteEntry(DataEntry dataentry)
    {
        try
        {
            super.deleteEntry(dataentry);
        }
        catch(IllegalArgumentException illegalargumentexception)
        {
            b b1 = new b();
            PageId pageid = getNextPage();
            if(pageid.pid != -1)
            {
                Minibase.BufferManager.pinPage(pageid, b1, false);
                boolean flag = b1.deleteEntry(dataentry);
                if(b1.getEntryCount() < 1)
                {
                    setNextPage(b1.getNextPage());
                    Minibase.BufferManager.unpinPage(pageid, flag);
                    Minibase.BufferManager.freePage(pageid);
                    return true;
                } else
                {
                    Minibase.BufferManager.unpinPage(pageid, flag);
                    return false;
                }
            } else
            {
                throw illegalargumentexception;
            }
        }
        return true;
    }

    public PageId _mthfor(int i)
    {
        b b1 = new b();
        PageId pageid = Minibase.BufferManager.newPage(b1, 1);
        PageId pageid1 = new PageId();
        b b2 = this;
        do
        {
            int j = b2.getEntryCount();
            for(int k = 0; k < j;)
            {
                DataEntry dataentry = b2.getEntryAt(k);
                if(dataentry.key.isHash(i))
                {
                    b1.insertEntry(dataentry);
                    b2.deleteEntry(dataentry);
                    j--;
                } else
                {
                    k++;
                }
            }

            if(pageid1.pid != -1)
                Minibase.BufferManager.unpinPage(pageid1, true);
            pageid1 = b2.getNextPage();
            if(pageid1.pid != -1)
                Minibase.BufferManager.pinPage(pageid1, b2, false);
        } while(pageid1.pid != -1);
        Minibase.BufferManager.unpinPage(pageid, true);
        return pageid;
    }
}
