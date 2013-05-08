// Decompiled by DJ v3.11.11.95 Copyright 2009 Atanas Neshkov  Date: 5/8/2013 10:58:54 PM
// Home Page: http://members.fortunecity.com/neshkov/dj.html  http://www.neshkov.com/dj.html - Check often for new version!
// Decompiler options: packimports(3) 
// Source File Name:   DirPage.java

package heap;

import global.PageId;

// Referenced classes of package heap:
//            HFPage

class DirPage extends HFPage
{

    public DirPage()
    {
        setType((short)10);
        setEntryCnt((short)0);
    }

    public short getEntryCnt()
    {
        return getShortValue(1022);
    }

    public void setEntryCnt(short entryCnt)
    {
        setShortValue(entryCnt, 1022);
    }

    public PageId getPageId(int slotno)
    {
        return new PageId(getIntValue(20 + slotno * 8 + 0));
    }

    public void setPageId(int slotno, PageId pageno)
    {
        setIntValue(pageno.pid, 20 + slotno * 8 + 0);
    }

    public short getRecCnt(int slotno)
    {
        return getShortValue(20 + slotno * 8 + 4);
    }

    public void setRecCnt(int slotno, short recCnt)
    {
        setShortValue(recCnt, 20 + slotno * 8 + 4);
    }

    public short getFreeCnt(int slotno)
    {
        return getShortValue(20 + slotno * 8 + 6);
    }

    public void setFreeCnt(int slotno, short freeCnt)
    {
        setShortValue(freeCnt, 20 + slotno * 8 + 6);
    }

    public void compact(int slotno)
    {
        int entryPos = 20 + slotno * 8;
        int succLen = 1022 - entryPos - 8;
        System.arraycopy(data, entryPos + 8, data, entryPos, succLen);
    }

    protected static final int ENTRY_SIZE = 8;
    protected static final int IX_PAGE_ID = 0;
    protected static final int IX_REC_CNT = 4;
    protected static final int IX_FREE_CNT = 6;
    protected static final int FOOTER_SIZE = 2;
    protected static final int ENTRY_COUNT = 1022;
    protected static final int MAX_ENTRIES = 125;
}
