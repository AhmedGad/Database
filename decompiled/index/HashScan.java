// Decompiled by DJ v3.11.11.95 Copyright 2009 Atanas Neshkov  Date: 5/8/2013 10:59:56 PM
// Home Page: http://members.fortunecity.com/neshkov/dj.html  http://www.neshkov.com/dj.html - Check often for new version!
// Decompiler options: packimports(3) 

package index;

import bufmgr.BufMgr;
import global.*;

// Referenced classes of package index:
//            HashIndex, a, SortedPage, DataEntry

public class HashScan
    implements GlobalConst
{

    protected HashScan(HashIndex hashindex, SearchKey searchkey)
    {
        int i = searchkey.getHash(hashindex.depth);
        _fldif = new SearchKey(searchkey);
        PageId pageid = new PageId(hashindex.headId.pid);
        a a1 = new a();
        for(; i >= 203; i -= 203)
        {
            Minibase.BufferManager.pinPage(pageid, a1, false);
            PageId pageid1 = a1.a();
            Minibase.BufferManager.unpinPage(pageid, false);
            pageid = pageid1;
        }

        Minibase.BufferManager.pinPage(pageid, a1, false);
        a = a1._mthif(i);
        Minibase.BufferManager.unpinPage(pageid, false);
        _flddo = new SortedPage();
        if(a.pid != -1)
        {
            Minibase.BufferManager.pinPage(a, _flddo, false);
            _fldfor = -1;
        }
    }

    protected void finalize()
        throws Throwable
    {
        if(a.pid != -1)
            close();
    }

    public void close()
    {
        if(a.pid != -1)
        {
            Minibase.BufferManager.unpinPage(a, false);
            a.pid = -1;
        }
    }

    public boolean hasNext()
    {
        while(a.pid != -1) 
        {
            _fldfor = _flddo.nextEntry(_fldif, _fldfor);
            if(_fldfor < 0)
            {
                PageId pageid = _flddo.getNextPage();
                Minibase.BufferManager.unpinPage(a, false);
                a = pageid;
                if(a.pid != -1)
                    Minibase.BufferManager.pinPage(a, _flddo, false);
            } else
            {
                return true;
            }
        }
        return false;
    }

    public RID getNext()
    {
        if(_fldfor < 0)
            throw new IllegalStateException("no more elements");
        try
        {
            return _flddo.getEntryAt(_fldfor).rid;
        }
        catch(IllegalArgumentException illegalargumentexception)
        {
            throw new IllegalStateException("no next entry");
        }
    }

    protected SearchKey _fldif;
    protected PageId a;
    protected SortedPage _flddo;
    protected int _fldfor;
}
