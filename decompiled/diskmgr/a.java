// Decompiled by DJ v3.11.11.95 Copyright 2009 Atanas Neshkov  Date: 5/8/2013 10:58:22 PM
// Home Page: http://members.fortunecity.com/neshkov/dj.html  http://www.neshkov.com/dj.html - Check often for new version!
// Decompiler options: packimports(3) 

package diskmgr;

import global.Page;

// Referenced classes of package diskmgr:
//            c

class a extends c
{

    public a()
    {
    }

    public a(Page page)
    {
        super(page);
    }

    public void _mthif(int i)
    {
        setIntValue(i, 1020);
    }

    public int _mthfor()
    {
        return getIntValue(1020);
    }

    protected static final int _fldtry = 1020;
}
