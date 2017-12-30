import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.impl.HttpSolrServer;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.apache.solr.common.SolrInputDocument;
import org.junit.Test;

import java.util.List;
import java.util.Map;

public class Main {

    public static void main(String[] args) {
        System.out.println("Hello World!");
    }

    //向索引库中添加索引
    @Test
    public void addDocument() throws Exception {
        //和solr服务器创建连接
        //参数：solr服务器的地址
        /**
         * 核心省略也会存入，不知道是默认第一个，还是按属性匹配
         * 高版本的solrj 类名SolrServer改名为SolrClient，里面在调用方法是可以选择核心
         * 比如solrClient.getById("collection1", 1)；不用在solr服务器地址上拼上核心名称
         */
        SolrServer solrServer = new HttpSolrServer("http://localhost:8080/solr/collection1");
        //创建一个文档对象
        SolrInputDocument document = new SolrInputDocument();
        //向文档中添加域
        //第一个参数：域的名称，域的名称必须是在schema.xml中定义的
        //第二个参数：域的值
        document.addField("id", "c0003");
        document.addField("title_ik", "测试核心core");
        document.addField("content_ik", "文档的内容");
//        document.addField("user_name", "用户名称");
        //把document对象添加到索引库中
        solrServer.add(document);
        //提交修改
        solrServer.commit();

    }

    //删除文档，根据id删除
    @Test
    public void deleteDocumentByid() throws Exception {
        //创建连接
        SolrServer solrServer = new HttpSolrServer("http://localhost:8080/solr/collection1");
        //根据id删除文档
        solrServer.deleteById("c0001");
        //提交修改
        solrServer.commit();
    }

    //根据查询条件删除文档
    @Test
    public void deleteDocumentByQuery() throws Exception {
        //创建连接
        SolrServer solrServer = new HttpSolrServer("http://localhost:8080/solr/collection1");
        //根据查询条件删除文档
        solrServer.deleteByQuery("id:c0003");
        //提交修改
        solrServer.commit();
    }

    //查询索引
    @Test
    public void queryIndex() throws Exception {
        //创建连接
        SolrServer solrServer = new HttpSolrServer("http://localhost:8080/solr/collection1");
        //创建一个query对象
        SolrQuery query = new SolrQuery();
        //设置查询条件
        query.setQuery("*:*");
        //执行查询
        QueryResponse queryResponse = solrServer.query(query);
        //取查询结果
        SolrDocumentList solrDocumentList = queryResponse.getResults();
        //共查询到商品数量
        System.out.println("共查询到用户数量:" + solrDocumentList.getNumFound());
        //遍历查询的结果
        for (SolrDocument solrDocument : solrDocumentList) {
            System.out.println(solrDocument.get("id"));
            System.out.println(solrDocument.get("address"));
            System.out.println(solrDocument.get("user_name"));
            System.out.println(solrDocument.get("office_name"));
            System.out.println(solrDocument.get("phone"));
        }
    }

    //复杂查询索引
    @Test
    public void queryIndex2() throws Exception {
        //创建连接
        SolrServer solrServer = new HttpSolrServer("http://localhost:8080/solr/collection1");
        //创建一个query对象
        SolrQuery query = new SolrQuery();
        //设置查询条件
        query.setQuery("*:*");
        //过滤条件
        query.setFilterQueries("login_name:jn_jsb");
        //排序条件
        query.setSort("id", SolrQuery.ORDER.asc);
        //分页处理
        query.setStart(0);
        query.setRows(10);
        //结果中域的列表
        query.setFields("id","user_name","login_name","address","phone");
        //设置默认搜索域
        query.set("df", "login_name");
        //高亮显示
        query.setHighlight(true);
        //高亮显示的域
        query.addHighlightField("id");
        //高亮显示的前缀
        query.setHighlightSimplePre("<span color='red'>");
        //高亮显示的后缀
        query.setHighlightSimplePost("</span>");
        //执行查询
        QueryResponse queryResponse = solrServer.query(query);
        //取查询结果
        SolrDocumentList solrDocumentList = queryResponse.getResults();
        //共查询到商品数量
        System.out.println("共查询到商品数量:" + solrDocumentList.getNumFound());
        //遍历查询的结果
        for (SolrDocument solrDocument : solrDocumentList) {
            System.out.println(solrDocument.get("id"));
            //取高亮显示
            String loginName = "";
            Map<String, Map<String, List<String>>> highlighting = queryResponse.getHighlighting();
            List<String> list = highlighting.get(solrDocument.get("id")).get("login_name");
            //判断是否有高亮内容
            if (null != list) {
                loginName = list.get(0);
            } else {
                loginName = (String) solrDocument.get("login_name");
            }

            System.out.println(loginName);
            System.out.println(solrDocument.get("user_name"));
            System.out.println(solrDocument.get("phone"));
            System.out.println(solrDocument.get("office_name"));
            System.out.println(solrDocument.get("id"));
        }
    }
}
