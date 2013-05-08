// Decompiled by DJ v3.11.11.95 Copyright 2009 Atanas Neshkov  Date: 5/8/2013 10:57:36 PM
// Home Page: http://members.fortunecity.com/neshkov/dj.html  http://www.neshkov.com/dj.html - Check often for new version!
// Decompiler options: packimports(3) 

package bufmgr;

import diskmgr.DiskMgr;
import global.*;
import java.util.HashMap;

// Referenced classes of package bufmgr:
//            b, c, a

public class BufMgr
    implements GlobalConst
{

    public BufMgr(int i)
    {
        _fldfor = new Page[i];
        _flddo = new b[i];
        for(int j = 0; j < i; j++)
        {
            _fldfor[j] = new Page();
            _flddo[j] = new b(j);
        }

        _fldif = new HashMap(i);
        a = new c(this);
    }

    public PageId newPage(Page page, int i)
    {
        PageId pageid = Minibase.DiskManager.allocate_page(i);
        try
        {
            pinPage(pageid, page, true);
        }
        catch(RuntimeException runtimeexception)
        {
            for(int j = 0; j < i; j++)
            {
                pageid.pid += j;
                Minibase.DiskManager.deallocate_page(pageid);
            }

            throw runtimeexception;
        }
        a._mthfor((b)_fldif.get(Integer.valueOf(pageid.pid)));
        return pageid;
    }

    public void freePage(PageId pageid)
    {
        b b1 = (b)_fldif.get(Integer.valueOf(pageid.pid));
        if(b1 != null)
        {
            if(b1._fldfor > 0)
                throw new IllegalArgumentException("Page currently pinned");
            _fldif.remove(Integer.valueOf(pageid.pid));
            b1._fldint.pid = -1;
            b1._fldfor = 0;
            b1._fldif = false;
            a._mthdo(b1);
        }
        Minibase.DiskManager.deallocate_page(pageid);
    }

    public void pinPage(PageId pageid, Page page, boolean flag)
    {
        b b1 = (b)_fldif.get(Integer.valueOf(pageid.pid));
        if(b1 != null)
            if(flag)
            {
                throw new IllegalArgumentException("Page pinned; PIN_MEMCPY not allowed");
            } else
            {
                b1._fldfor++;
                a._mthif(b1);
                page.setPage(_fldfor[b1.a]);
                return;
            }
        int i = a.a();
        if(i < 0)
            throw new IllegalStateException("Buffer pool exceeded");
        b1 = _flddo[i];
        if(b1._fldint.pid != -1)
        {
            _fldif.remove(Integer.valueOf(b1._fldint.pid));
            if(b1._fldif)
                Minibase.DiskManager.write_page(b1._fldint, _fldfor[i]);
        }
        if(flag)
            _fldfor[i].copyPage(page);
        else
            Minibase.DiskManager.read_page(pageid, _fldfor[i]);
        page.setPage(_fldfor[i]);
        b1._fldint.pid = pageid.pid;
        b1._fldfor = 1;
        b1._fldif = false;
        _fldif.put(Integer.valueOf(pageid.pid), b1);
        a._mthif(b1);
    }

    public void unpinPage(PageId pageid, boolean flag)
    {
        b b1 = (b)_fldif.get(Integer.valueOf(pageid.pid));
        if(b1 == null)
            throw new IllegalArgumentException("Page not present");
        if(b1._fldfor == 0)
        {
            throw new IllegalArgumentException("Page not pinned");
        } else
        {
            b1._fldfor--;
            b1._fldif |= flag;
            a.a(b1);
            return;
        }
    }

    public void flushPage(PageId pageid)
    {
        a(pageid);
    }

    public void flushAllPages()
    {
        a(null);
    }

    protected void a(PageId pageid)
    {
        for(int i = 0; i < _fldfor.length; i++)
            if((pageid == null || _flddo[i]._fldint.pid == pageid.pid) && _flddo[i]._fldif)
            {
                Minibase.DiskManager.write_page(_flddo[i]._fldint, _fldfor[i]);
                _flddo[i]._fldif = false;
            }

    }

    public int getNumBuffers()
    {
        return _fldfor.length;
    }

    public int getNumUnpinned()
    {
        int i = 0;
        for(int j = 0; j < _fldfor.length; j++)
            if(_flddo[j]._fldfor == 0)
                i++;

        return i;
    }

    protected Page _fldfor[];
    protected b _flddo[];
    protected HashMap _fldif;
    protected a a;
}
