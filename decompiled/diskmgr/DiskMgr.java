// Decompiled by DJ v3.11.11.95 Copyright 2009 Atanas Neshkov  Date: 5/8/2013 10:58:22 PM
// Home Page: http://members.fortunecity.com/neshkov/dj.html  http://www.neshkov.com/dj.html - Check often for new version!
// Decompiler options: packimports(3) 

package diskmgr;

import bufmgr.BufMgr;
import global.*;
import java.io.*;

// Referenced classes of package diskmgr:
//            a, c

public class DiskMgr
    implements GlobalConst
{

    public DiskMgr()
    {
    }

    public int getReadCount()
    {
        return _fldfor;
    }

    public int getWriteCount()
    {
        return _fldnew;
    }

    public int getAllocCount()
    {
        int i = 0;
        int j = 0;
        PageId pageid = new PageId();
        Page page = new Page();
        int k = ((_fldint + 8192) - 1) / 8192;
        for(int l = 0; l < k; l++)
        {
            pageid.pid = 1 + l;
            Minibase.BufferManager.pinPage(pageid, page, false);
            int i1 = _fldint - l * 8192;
            if(i1 > 8192)
                i1 = 8192;
            byte abyte0[] = page.getData();
            for(int j1 = 0; i1 > 0; j1++)
            {
                for(int k1 = 1; k1 < 256 && i1 > 0;)
                {
                    int l1 = abyte0[j1] & k1;
                    if(l1 != 0)
                        i++;
                    k1 <<= 1;
                    i1--;
                    j++;
                }

            }

            Minibase.BufferManager.unpinPage(pageid, false);
        }

        return i;
    }

    public void createDB(String s, int i)
    {
        _flddo = s;
        _fldint = i <= 2 ? 2 : i;
        File file = new File(_flddo);
        file.delete();
        try
        {
            _fldif = new RandomAccessFile(s, "rw");
            _fldif.seek(_fldint * 1024 - 1);
            _fldif.writeByte(0);
        }
        catch(IOException ioexception)
        {
            Minibase.haltSystem(ioexception);
        }
        PageId pageid = new PageId(0);
        a a1 = new a();
        Minibase.BufferManager.pinPage(pageid, a1, true);
        a1._mthif(_fldint);
        Minibase.BufferManager.unpinPage(pageid, true);
        int j = ((_fldint + 8192) - 1) / 8192;
        a(pageid, 1 + j, 1);
    }

    public void openDB(String s)
    {
        _flddo = s;
        try
        {
            _fldif = new RandomAccessFile(s, "rw");
        }
        catch(IOException ioexception)
        {
            Minibase.haltSystem(ioexception);
        }
        _fldint = 2;
        PageId pageid = new PageId(0);
        Page page = new Page();
        Minibase.BufferManager.pinPage(pageid, page, false);
        a a1 = new a(page);
        _fldint = a1._mthfor();
        Minibase.BufferManager.unpinPage(pageid, false);
    }

    public void closeDB()
    {
        try
        {
            Minibase.BufferManager.flushAllPages();
            _fldif.close();
        }
        catch(IOException ioexception)
        {
            Minibase.haltSystem(ioexception);
        }
    }

    public void destroyDB()
    {
        closeDB();
        File file = new File(_flddo);
        file.delete();
    }

    public PageId allocate_page()
    {
        return allocate_page(1);
    }

    public PageId allocate_page(int i)
    {
        if(i < 1 || i > _fldint)
            throw new IllegalArgumentException("Invalid run size; allocate aborted");
        int j = ((_fldint + 8192) - 1) / 8192;
        int k = 0;
        int l = 0;
        PageId pageid = new PageId();
        Page page = new Page();
        for(int i1 = 0; i1 < j; i1++)
        {
            pageid.pid = i1 + 1;
            Minibase.BufferManager.pinPage(pageid, page, false);
            int j1 = _fldint - i1 * 8192;
            if(j1 > 8192)
                j1 = 8192;
            byte abyte0[] = page.getData();
            for(int k1 = 0; j1 > 0 && l < i; k1++)
            {
                Byte byte1 = new Byte((new Integer(1)).byteValue());
                byte byte0 = byte1.byteValue();
                for(; byte1.intValue() != 0 && j1 > 0 && l < i; j1--)
                {
                    if((abyte0[k1] & byte0) != 0)
                    {
                        k += l + 1;
                        l = 0;
                    } else
                    {
                        l++;
                    }
                    byte0 <<= 1;
                    byte1 = new Byte(byte0);
                }

            }

            Minibase.BufferManager.unpinPage(pageid, false);
        }

        if(l < i)
        {
            throw new IllegalStateException("Not enough space left; allocate aborted");
        } else
        {
            PageId pageid1 = new PageId(k);
            a(pageid1, i, 1);
            return pageid1;
        }
    }

    public void deallocate_page(PageId pageid)
    {
        deallocate_page(pageid, 1);
    }

    public void deallocate_page(PageId pageid, int i)
    {
        if(pageid.pid < 0 || pageid.pid >= _fldint)
            throw new IllegalArgumentException("Invalid page number; deallocate aborted");
        if(i < 1)
        {
            throw new IllegalArgumentException("Invalid run size; deallocate aborted");
        } else
        {
            a(pageid, i, 0);
            return;
        }
    }

    public void read_page(PageId pageid, Page page)
    {
        if(pageid.pid < 0 || pageid.pid >= _fldint)
            throw new IllegalArgumentException("Invalid page number; read aborted");
        try
        {
            _fldif.seek(pageid.pid * 1024);
            _fldif.read(page.getData());
            _fldfor++;
        }
        catch(IOException ioexception)
        {
            Minibase.haltSystem(ioexception);
        }
    }

    public void write_page(PageId pageid, Page page)
    {
        if(pageid.pid < 0 || pageid.pid >= _fldint)
            throw new IllegalArgumentException("Invalid page number; write aborted");
        try
        {
            _fldif.seek(pageid.pid * 1024);
            _fldif.write(page.getData());
            _fldnew++;
        }
        catch(IOException ioexception)
        {
            Minibase.haltSystem(ioexception);
        }
    }

    public void add_file_entry(String s, PageId pageid)
    {
        if(s.length() > 50)
            throw new IllegalArgumentException("Filename too long; add entry aborted");
        if(pageid.pid < 0 || pageid.pid >= _fldint)
            throw new IllegalArgumentException("Invalid page number; add entry aborted");
        if(get_file_entry(s) != null)
            throw new IllegalArgumentException("File entry already exists; add entry aborted");
        boolean flag = false;
        int i = 0;
        c c1 = new c();
        PageId pageid1 = new PageId();
        PageId pageid2 = new PageId();
        PageId pageid3 = new PageId(0);
        do
        {
            pageid1.pid = pageid3.pid;
            Minibase.BufferManager.pinPage(pageid1, c1, false);
            pageid3 = c1.a();
            int j;
            for(j = 0; j < c1._mthif(); j++)
            {
                c1.a(pageid2, j);
                if(pageid2.pid == -1)
                    break;
            }

            if(j < c1._mthif())
            {
                i = j;
                flag = true;
            } else
            if(pageid3.pid != -1)
                Minibase.BufferManager.unpinPage(pageid1, false);
        } while(pageid3.pid != -1 && !flag);
        if(!flag)
        {
            PageId pageid4 = allocate_page();
            c1.a(pageid4);
            Minibase.BufferManager.unpinPage(pageid1, true);
            pageid1.pid = pageid4.pid;
            Minibase.BufferManager.pinPage(pageid1, c1, true);
            c1._mthdo();
            i = 0;
        }
        c1.a(s, pageid, i);
        Minibase.BufferManager.unpinPage(pageid1, true);
    }

    public void delete_file_entry(String s)
    {
        if(get_file_entry(s) == null)
            throw new IllegalArgumentException("File entry not found; delete entry aborted");
        boolean flag = false;
        int i = 0;
        c c1 = new c();
        PageId pageid = new PageId();
        PageId pageid1 = new PageId();
        PageId pageid2 = new PageId(0);
        do
        {
            pageid.pid = pageid2.pid;
            Minibase.BufferManager.pinPage(pageid, c1, false);
            pageid2 = c1.a();
            int j = 0;
            Object obj = null;
            for(; j < c1._mthif(); j++)
            {
                String s1 = c1.a(pageid1, j);
                if(pageid1.pid != -1 && s1.compareToIgnoreCase(s) == 0)
                    break;
            }

            if(j < c1._mthif())
            {
                i = j;
                flag = true;
            } else
            {
                Minibase.BufferManager.unpinPage(pageid, false);
            }
        } while(pageid2.pid != -1 && !flag);
        pageid1.pid = -1;
        c1.a("\0", pageid1, i);
        Minibase.BufferManager.unpinPage(pageid, true);
    }

    public PageId get_file_entry(String s)
    {
        boolean flag = false;
        int i = 0;
        c c1 = new c();
        PageId pageid = new PageId();
        PageId pageid1 = new PageId();
        PageId pageid2 = new PageId(0);
        do
        {
            pageid.pid = pageid2.pid;
            Minibase.BufferManager.pinPage(pageid, c1, false);
            pageid2 = c1.a();
            int j;
            for(j = 0; j < c1._mthif(); j++)
            {
                String s1 = c1.a(pageid1, j);
                if(pageid1.pid != -1 && s1.compareToIgnoreCase(s) == 0)
                    break;
            }

            if(j < c1._mthif())
            {
                i = j;
                flag = true;
            }
            Minibase.BufferManager.unpinPage(pageid, false);
        } while(pageid2.pid != -1 && !flag);
        if(!flag)
        {
            return null;
        } else
        {
            PageId pageid3 = new PageId();
            c1.a(pageid3, i);
            return pageid3;
        }
    }

    public void print_space_map()
    {
        int i = ((_fldint + 8192) - 1) / 8192;
        int j = 0;
        PageId pageid = new PageId();
        System.out.println((new StringBuilder("num_map_pages = ")).append(i).toString());
        System.out.print((new StringBuilder("num_pages = ")).append(_fldint).toString());
        for(int k = 0; k < i; k++)
        {
            pageid.pid = 1 + k;
            Page page = new Page();
            Minibase.BufferManager.pinPage(pageid, page, false);
            int l = _fldint - k * 8192;
            if(l > 8192)
                l = 8192;
            System.out.println((new StringBuilder("\n\nnum_bits_this_page = ")).append(l).append("\n").toString());
            if(k > 0)
                System.out.print("\t");
            int i1 = 0;
            byte abyte0[] = page.getData();
            while(l > 0) 
            {
                for(int j1 = 1; j1 < 256 && l > 0;)
                {
                    int k1 = abyte0[i1] & j1;
                    if(j % 10 == 0)
                        if(j % 50 == 0)
                        {
                            if(j > 0)
                                System.out.println("\n");
                            System.out.print((new StringBuilder("\t")).append(j).append(": ").toString());
                        } else
                        {
                            System.out.print(' ');
                        }
                    if(k1 != 0)
                        System.out.print("1");
                    else
                        System.out.print("0");
                    j1 <<= 1;
                    l--;
                    j++;
                }

                i1++;
            }
            Minibase.BufferManager.unpinPage(pageid, false);
        }

        System.out.println();
    }

    protected void a(PageId pageid, int i, int j)
    {
        int k = pageid.pid / 8192 + 1;
        int l = ((pageid.pid + i) - 1) / 8192 + 1;
        int i1 = pageid.pid % 8192;
        for(PageId pageid1 = new PageId(k); pageid1.pid <= l;)
        {
            Page page = new Page();
            Minibase.BufferManager.pinPage(pageid1, page, false);
            byte abyte0[] = page.getData();
            int j1 = i1 / 8;
            int k1 = i1 % 8;
            int l1 = (i1 + i) - 1;
            if(l1 >= 8192)
                l1 = 8191;
            int i2 = l1 / 8;
            for(int j2 = j1; j2 <= i2;)
            {
                int k2 = 8 - k1;
                int l2 = i <= k2 ? i : k2;
                int i3 = 1;
                i3 = (i3 << l2) - 1 << k1;
                Integer integer = new Integer(i3);
                Byte byte1 = new Byte(integer.byteValue());
                byte byte0 = byte1.byteValue();
                if(j == 1)
                {
                    int j3 = abyte0[j2] | byte0;
                    Integer integer1 = new Integer(j3);
                    abyte0[j2] = integer1.byteValue();
                } else
                {
                    int k3 = abyte0[j2] & (0xff ^ byte0);
                    Integer integer2 = new Integer(k3);
                    abyte0[j2] = integer2.byteValue();
                }
                i -= l2;
                j2++;
                k1 = 0;
            }

            Minibase.BufferManager.unpinPage(pageid1, true);
            pageid1.pid = pageid1.pid + 1;
            i1 = 0;
        }

    }

    protected static final int a = 8192;
    protected String _flddo;
    protected int _fldint;
    protected RandomAccessFile _fldif;
    protected int _fldfor;
    protected int _fldnew;
}
