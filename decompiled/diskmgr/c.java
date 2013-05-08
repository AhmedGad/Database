// Decompiled by DJ v3.11.11.95 Copyright 2009 Atanas Neshkov  Date: 5/8/2013 10:58:22 PM
// Home Page: http://members.fortunecity.com/neshkov/dj.html  http://www.neshkov.com/dj.html - Check often for new version!
// Decompiler options: packimports(3) 

package diskmgr;

import global.Page;
import global.PageId;

// Referenced classes of package diskmgr:
//            a

class c extends Page
{

    public c()
    {
        _mthdo();
    }

    public c(Page page)
    {
        super(page.getData());
    }

    protected void _mthdo()
    {
        PageId pageid = new PageId();
        a(pageid);
        byte byte0 = 16;
        if(this instanceof a)
            byte0 = 20;
        int i = (1024 - byte0) / 56;
        a(i);
        for(int j = 0; j < i; j++)
        {
            int k = 8 + j * 56;
            setIntValue(-1, k);
        }

    }

    public PageId a()
    {
        PageId pageid = new PageId();
        pageid.pid = getIntValue(0);
        return pageid;
    }

    public void a(PageId pageid)
    {
        setIntValue(pageid.pid, 0);
    }

    public int _mthif()
    {
        return getIntValue(4);
    }

    public void a(int i)
    {
        setIntValue(i, 4);
    }

    public String a(PageId pageid, int i)
    {
        int j = 8 + i * 56;
        pageid.pid = getIntValue(j);
        return getStringValue(j + 4, 52);
    }

    public void a(String s, PageId pageid, int i)
    {
        int j = 8 + i * 56;
        setIntValue(pageid.pid, j);
        setStringValue(s, j + 4);
    }

    protected static final int _fldint = 0;
    protected static final int a = 4;
    protected static final int _fldfor = 8;
    protected static final int _fldnew = 56;
    protected static final int _flddo = 16;
    protected static final int _fldif = 20;
}
