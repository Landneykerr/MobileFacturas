package Adapter;

/**
 * Created by JULIANEDUARDO on 10/02/2015.
 */
public class DetalleFourItems {
    protected String 	item1;
    protected String 	item2;
    protected String 	item3;
    protected String 	item4;
    protected long 		Id;

    public DetalleFourItems(String _item1, String _item2, String _item3, String _item4){
        super();
        this.item1 	= _item1;
        this.item2 	= _item2;
        this.item3 	= _item3;
        this.item4 	= _item4;
    }

    public String getItem1(){
        return this.item1;
    }

    public String getItem2(){
        return this.item2;
    }

    public String getItem3(){
        return this.item3;
    }

    public String getItem4(){
        return this.item4;
    }



    public long getId(){
        return this.Id;
    }

    public void setItem1(String _item){
        this.item1 = _item;
    }

    public void setItem2(String _item){
        this.item2 = _item;
    }

    public void setItem3(String _item){
        this.item3 = _item;
    }

    public void setItem4(String _item){
        this.item4 = _item;
    }


    public void setId(long id){
        this.Id= id;
    }
}
