// Decompiled by DJ v3.11.11.95 Copyright 2009 Atanas Neshkov  Date: 5/8/2013 10:59:42 PM
// Home Page: http://members.fortunecity.com/neshkov/dj.html  http://www.neshkov.com/dj.html - Check often for new version!
// Decompiler options: packimports(3) 

package index;

import bufmgr.BufMgr;
import global.*;

// Referenced classes of package index:
//            HashIndex, SortedPage, DataEntry

public class BucketScan
    implements GlobalConst
{

    protected BucketScan(HashIndex hashindex)
    {
        _fldfor = hashindex.headId;
        _fldtry = new Page();
        Minibase.BufferManager.pinPage(_fldfor, _fldtry, false);
        _fldint = -1;
        a();
    }

    protected void finalize()
        throws Throwable
    {
        if(_fldfor != null)
            close();
    }

    public void close()
    {
        if(_fldfor != null)
        {
            Minibase.BufferManager.unpinPage(_fldfor, false);
            _fldfor = null;
        }
        if(a != null)
        {
            Minibase.BufferManager.unpinPage(a, false);
            a = null;
        }
    }

    public boolean hasNext()
    {
        while(a != null) 
        {
            if(_fldnew < _flddo.getEntryCount() - 1)
                return true;
            PageId pageid = _flddo.getNextPage();
            Minibase.BufferManager.unpinPage(a, false);
            if(pageid.pid != -1)
            {
                a = pageid;
                Minibase.BufferManager.pinPage(a, _flddo, false);
                _fldnew = -1;
            } else
            {
                a = null;
                a();
            }
        }
        return false;
    }

    public RID getNext()
    {
        if(!hasNext())
        {
            throw new IllegalStateException("no more entries");
        } else
        {
            _fldnew++;
            DataEntry dataentry = _flddo.getEntryAt(_fldnew);
            _fldif = dataentry.key;
            return dataentry.rid;
        }
    }

    public SearchKey getLastKey()
    {
        return _fldif;
    }

    public int getNextHash()
    {
        if(!hasNext())
            return 128;
        else
            return _fldint;
    }

    protected void a()
    {
        while(_fldint < 128) 
        {
            _fldint++;
            int i = _fldtry.getIntValue(6 + _fldint * 5);
            if(i == -1)
                continue;
            a = new PageId(i);
            _flddo = new SortedPage();
            Minibase.BufferManager.pinPage(a, _flddo, false);
            _fldnew = -1;
            break;
        }
    }

    protected PageId _fldfor;
    protected Page _fldtry;
    protected int _fldint;
    protected PageId a;
    protected SortedPage _flddo;
    protected int _fldnew;
    protected SearchKey _fldif;
}
