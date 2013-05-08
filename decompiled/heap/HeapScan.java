// Decompiled by DJ v3.11.11.95 Copyright 2009 Atanas Neshkov  Date: 5/8/2013 10:58:54 PM
// Home Page: http://members.fortunecity.com/neshkov/dj.html  http://www.neshkov.com/dj.html - Check often for new version!
// Decompiler options: packimports(3) 
// Source File Name:   HeapScan.java

package heap;

import bufmgr.BufMgr;
import global.*;

// Referenced classes of package heap:
//            DirPage, HeapFile, DataPage

public class HeapScan
    implements GlobalConst
{

    protected HeapScan(HeapFile hf)
    {
        dirPage = new DirPage();
        Minibase.BufferManager.pinPage(hf.headId, dirPage, false);
        count = dirPage.getEntryCnt();
        index = -1;
        dataPage = null;
        curRid = null;
    }

    protected void finalize()
        throws Throwable
    {
        if(dirPage != null)
            close();
    }

    public void close()
    {
        if(dataPage != null)
        {
            Minibase.BufferManager.unpinPage(dataPage.getCurPage(), false);
            dataPage = null;
        }
        if(dirPage != null)
        {
            Minibase.BufferManager.unpinPage(dirPage.getCurPage(), false);
            dirPage = null;
        }
        count = -1;
        index = -1;
        curRid = null;
    }

    public boolean hasNext()
    {
        if(curRid != null && dataPage.nextRecord(curRid) != null)
            return true;
        if(index < count - 1)
            return true;
        return dirPage.getNextPage().pid != -1;
    }

    public byte[] getNext(RID rid)
    {
        if(curRid != null)
        {
            curRid = dataPage.nextRecord(curRid);
            if(curRid != null)
            {
                rid.copyRID(curRid);
                return dataPage.selectRecord(rid);
            }
            Minibase.BufferManager.unpinPage(dataPage.getCurPage(), false);
        }
        if(index < count - 1)
        {
            if(dataPage == null)
                dataPage = new DataPage();
            index++;
            Minibase.BufferManager.pinPage(dirPage.getPageId(index), dataPage, false);
            curRid = dataPage.firstRecord();
            if(curRid != null)
            {
                rid.copyRID(curRid);
                return dataPage.selectRecord(rid);
            }
        }
        if(dirPage.getNextPage().pid != -1)
        {
            PageId nextId = dirPage.getNextPage();
            Minibase.BufferManager.unpinPage(dirPage.getCurPage(), false);
            Minibase.BufferManager.pinPage(nextId, dirPage, false);
            count = dirPage.getEntryCnt();
            index = -1;
            curRid = null;
            return getNext(rid);
        } else
        {
            throw new IllegalStateException("No more elements");
        }
    }

    protected DirPage dirPage;
    protected int count;
    protected int index;
    protected DataPage dataPage;
    protected RID curRid;
}
