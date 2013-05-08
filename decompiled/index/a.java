// Decompiled by DJ v3.11.11.95 Copyright 2009 Atanas Neshkov  Date: 5/8/2013 10:59:31 PM
// Home Page: http://members.fortunecity.com/neshkov/dj.html  http://www.neshkov.com/dj.html - Check often for new version!
// Decompiler options: packimports(3) 

package index;

import global.Page;
import global.PageId;

class a extends Page
{

    public a()
    {
        _mthdo();
    }

    public a(Page page)
    {
        super(page.getData());
    }

    protected void _mthdo()
    {
        setShortValue((short)128, 0);
        setIntValue(-1, 2);
        for(int i = 0; i < 203; i++)
            setIntValue(-1, 6 + i * 5 + 0);

    }

    public short _mthif()
    {
        return getShortValue(0);
    }

    public PageId a()
    {
        return new PageId(getIntValue(2));
    }

    public void a(PageId pageid)
    {
        setIntValue(pageid.pid, 2);
    }

    public PageId _mthif(int i)
    {
        return new PageId(getIntValue(6 + i * 5 + 0));
    }

    public void a(int i, PageId pageid)
    {
        setIntValue(pageid.pid, 6 + i * 5 + 0);
    }

    public byte a(int i)
    {
        return data[6 + i * 5 + 4];
    }

    public void a(int i, byte byte0)
    {
        data[6 + i * 5 + 4] = byte0;
    }

    protected static final int _fldif = 0;
    protected static final int _fldbyte = 2;
    protected static final int _fldtry = 0;
    protected static final int _fldfor = 4;
    protected static final int _fldint = 6;
    protected static final int _flddo = 5;
    protected static final int a = 203;
    protected static final short _fldnew = 128;
}
