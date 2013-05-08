// Decompiled by DJ v3.11.11.95 Copyright 2009 Atanas Neshkov  Date: 5/8/2013 10:57:36 PM
// Home Page: http://members.fortunecity.com/neshkov/dj.html  http://www.neshkov.com/dj.html - Check often for new version!
// Decompiler options: packimports(3) 

package bufmgr;


// Referenced classes of package bufmgr:
//            a, b, BufMgr

class c extends a
{

    public c(BufMgr bufmgr)
    {
        super(bufmgr);
        for(int i = 0; i < a.length; i++)
            a[i]._flddo = 10;

        _fldfor = -1;
    }

    public void _mthfor(b b1)
    {
    }

    public void _mthdo(b b1)
    {
        b1._flddo = 10;
    }

    public void _mthif(b b1)
    {
        b1._flddo = 12;
    }

    public void a(b b1)
    {
        if(b1._fldfor == 0)
            b1._flddo = 11;
    }

    public int a()
    {
        int i = 0;
        do
        {
            _fldfor = (_fldfor + 1) % a.length;
            if(i++ >= 2 * a.length)
                return -1;
            if(a[_fldfor]._flddo == 11)
                a[_fldfor]._flddo = 10;
        } while(a[_fldfor]._flddo != 10);
        return _fldfor;
    }

    protected static final int _flddo = 10;
    protected static final int _fldif = 11;
    protected static final int _fldint = 12;
    protected int _fldfor;
}
