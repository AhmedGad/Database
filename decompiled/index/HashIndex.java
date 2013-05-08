// Decompiled by DJ v3.11.11.95 Copyright 2009 Atanas Neshkov  Date: 5/8/2013 10:59:51 PM
// Home Page: http://members.fortunecity.com/neshkov/dj.html  http://www.neshkov.com/dj.html - Check often for new version!
// Decompiler options: packimports(3) 

package index;

import bufmgr.BufMgr;
import diskmgr.DiskMgr;
import global.*;
import java.io.PrintStream;

// Referenced classes of package index:
//            a, b, DataEntry, BucketScan, 
//            HashScan

public class HashIndex
    implements GlobalConst
{

    public HashIndex(String s)
    {
        fileName = s;
        boolean flag = false;
        if(s != null)
        {
            headId = Minibase.DiskManager.get_file_entry(s);
            if(headId != null)
                flag = true;
        }
        if(!flag)
        {
            a a1 = new a();
            headId = Minibase.BufferManager.newPage(a1, 1);
            Minibase.BufferManager.unpinPage(headId, true);
            if(s != null)
                Minibase.DiskManager.add_file_entry(s, headId);
        }
        _mthdo();
    }

    protected void finalize()
        throws Throwable
    {
        if(fileName == null)
            deleteFile();
    }

    protected void _mthdo()
    {
        int i = 0;
        PageId pageid = new PageId(headId.pid);
        a a1 = new a();
        PageId pageid1;
        for(; pageid.pid != -1; pageid = pageid1)
        {
            Minibase.BufferManager.pinPage(pageid, a1, false);
            i += a1._mthif();
            pageid1 = a1.a();
            Minibase.BufferManager.unpinPage(pageid, false);
        }

        depth = (byte)(int)(Math.log(i) / Math.log(2D));
    }

    public void deleteFile()
    {
        PageId pageid = new PageId(headId.pid);
        a a1 = new a();
        b b1 = new b();
        PageId pageid1;
        for(; pageid.pid != -1; pageid = pageid1)
        {
            Minibase.BufferManager.pinPage(pageid, a1, false);
            short word0 = a1._mthif();
            for(int i = 0; i < word0; i++)
            {
                PageId pageid3;
                for(PageId pageid2 = a1._mthif(i); pageid2.pid != -1; pageid2 = pageid3)
                {
                    Minibase.BufferManager.pinPage(pageid2, b1, false);
                    pageid3 = b1.getNextPage();
                    Minibase.BufferManager.unpinPage(pageid2, false);
                    Minibase.BufferManager.freePage(pageid2);
                }

            }

            pageid1 = a1.a();
            Minibase.BufferManager.unpinPage(pageid, false);
            Minibase.BufferManager.freePage(pageid);
        }

        if(fileName != null)
            Minibase.DiskManager.delete_file_entry(fileName);
    }

    public void insertEntry(SearchKey searchkey, RID rid)
    {
        DataEntry dataentry = new DataEntry(searchkey, rid);
        if(dataentry.getLength() > 1012)
            throw new IllegalArgumentException("entry too large");
        int i = searchkey.getHash(depth);
        PageId pageid = new PageId(headId.pid);
        a a1 = new a();
        for(; i >= 203; i -= 203)
        {
            Minibase.BufferManager.pinPage(pageid, a1, false);
            PageId pageid1 = a1.a();
            Minibase.BufferManager.unpinPage(pageid, false);
            pageid = pageid1;
        }

        Minibase.BufferManager.pinPage(pageid, a1, false);
        PageId pageid2 = a1._mthif(i);
        b b1 = new b();
        if(pageid2.pid != -1)
        {
            Minibase.BufferManager.pinPage(pageid2, b1, false);
            Minibase.BufferManager.unpinPage(pageid, false);
        } else
        {
            pageid2 = Minibase.BufferManager.newPage(b1, 1);
            a1.a(i, pageid2);
            Minibase.BufferManager.unpinPage(pageid, true);
        }
        boolean flag = b1.insertEntry(dataentry);
        Minibase.BufferManager.unpinPage(pageid2, flag);
    }

    protected void _mthif()
    {
    }

    public void deleteEntry(SearchKey searchkey, RID rid)
    {
        int i = searchkey.getHash(depth);
        DataEntry dataentry = new DataEntry(searchkey, rid);
        PageId pageid = new PageId(headId.pid);
        a a1 = new a();
        for(; i >= 203; i -= 203)
        {
            Minibase.BufferManager.pinPage(pageid, a1, false);
            PageId pageid1 = a1.a();
            Minibase.BufferManager.unpinPage(pageid, false);
            pageid = pageid1;
        }

        Minibase.BufferManager.pinPage(pageid, a1, false);
        PageId pageid2 = a1._mthif(i);
        Minibase.BufferManager.unpinPage(pageid, false);
        b b1 = new b();
        if(pageid2.pid != -1)
            Minibase.BufferManager.pinPage(pageid2, b1, false);
        else
            throw new IllegalArgumentException("entry doesn't exist");
        try
        {
            boolean flag = b1.deleteEntry(dataentry);
            Minibase.BufferManager.unpinPage(pageid2, flag);
        }
        catch(IllegalArgumentException illegalargumentexception)
        {
            Minibase.BufferManager.unpinPage(pageid2, false);
            throw illegalargumentexception;
        }
    }

    protected void a()
    {
    }

    public BucketScan openScan()
    {
        return new BucketScan(this);
    }

    public HashScan openScan(SearchKey searchkey)
    {
        return new HashScan(this, searchkey);
    }

    public String toString()
    {
        return fileName;
    }

    public void printSummary()
    {
        System.out.println();
        String s = fileName == null ? "(temp)" : fileName;
        System.out.println(s);
        for(int i = 0; i < s.length(); i++)
            System.out.print('-');

        System.out.println();
        int j = 0;
        PageId pageid = new PageId(headId.pid);
        a a1 = new a();
        b b1 = new b();
        PageId pageid1;
        for(; pageid.pid != -1; pageid = pageid1)
        {
            Minibase.BufferManager.pinPage(pageid, a1, false);
            short word0 = a1._mthif();
            for(int l = 0; l < word0; l++)
            {
                String s1 = Integer.toString(l, 2);
                for(int i1 = 0; i1 < depth - s1.length(); i1++)
                    System.out.print('0');

                System.out.print((new StringBuilder(String.valueOf(s1))).append(" : ").toString());
                PageId pageid2 = a1._mthif(l);
                if(pageid2.pid != -1)
                {
                    Minibase.BufferManager.pinPage(pageid2, b1, false);
                    int j1 = b1._mthif();
                    System.out.println(j1);
                    j += j1;
                    Minibase.BufferManager.unpinPage(pageid2, false);
                } else
                {
                    System.out.println("null");
                }
            }

            pageid1 = a1.a();
            Minibase.BufferManager.unpinPage(pageid, false);
        }

        for(int k = 0; k < s.length(); k++)
            System.out.print('-');

        System.out.println();
        System.out.println((new StringBuilder("Total : ")).append(j).toString());
    }

    protected String fileName;
    protected PageId headId;
    protected byte depth;
}
