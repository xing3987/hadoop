package hadoop.mapreduce.topnadvance;

import org.apache.hadoop.io.Writable;
import org.apache.hadoop.io.WritableComparable;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public class Product implements WritableComparable<Product> {

    private String order;
    private String uid;
    private String name;
    private Double price;
    private Integer count;

    public Product() {
    }

    public Product(String order, String uid, String name, Double price, Integer count) {
        this.order = order;
        this.uid = uid;
        this.name = name;
        this.price = price;
        this.count = count;
    }

    public String getOrder() {
        return order;
    }

    public void setOrder(String order) {
        this.order = order;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }

    @Override
    public void write(DataOutput output) throws IOException {
        output.writeUTF(this.order);
        output.writeUTF(this.uid);
        output.writeUTF(this.name);
        output.writeDouble(this.price);
        output.writeInt(count);
    }

    @Override
    public void readFields(DataInput input) throws IOException {
        this.order = input.readUTF();
        this.uid = input.readUTF();
        this.name = input.readUTF();
        this.price = input.readDouble();
        this.count = input.readInt();
    }

    @Override
    public String toString() {
        return order + "," + uid + ',' + name + ',' + price + ',' + count;
    }

    /**
     * 先按orderid排序，orderid相同的按总价大小排序
     *
     * @param o
     * @return
     */
    @Override
    public int compareTo(Product o) {
        if (this.order.equals(o.getOrder())) {
            return o.getPrice() * o.getCount() - this.price * this.count > 0 ? 1 : -1;
        } else {
            return o.order.compareTo(this.order);
        }
    }
}
