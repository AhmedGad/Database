// Decompiled by DJ v3.11.11.95 Copyright 2009 Atanas Neshkov  Date: 5/8/2013 10:57:36 PM
// Home Page: http://members.fortunecity.com/neshkov/dj.html  http://www.neshkov.com/dj.html - Check often for new version!
// Decompiler options: packimports(3) 

package bufmgr;

import global.GlobalConst;

// Referenced classes of package bufmgr:
//            BufMgr, b

abstract class a
    implements GlobalConst
{

    protected a(BufMgr bufmgr)
    {
        a = bufmgr._flddo;
    }

    public abstract void _mthfor(b b);

    public abstract void _mthdo(b b);

    public abstract void _mthif(b b);

    public abstract void a(b b);

    public abstract int a();

    protected b a[];
}
