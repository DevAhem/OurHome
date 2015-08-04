package pub;

import javax.sql.DataSource;

import org.together.pub.util.BeanUtils;
import org.together.pub.util.XMLUtils;

import com.alibaba.druid.pool.DruidDataSourceFactory;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;

public class TestModule extends AbstractModule {

	@Override
	protected void configure() {
	}

	@Provides
	public DataSource getDataSource() {
		DataSource dataSource = null;
		Context context = XMLUtils.XMLToObject(Context.class, "WebContent/META-INF/context.xml");

		Resource resource = context.Resource.get(0);

		try {
			dataSource = DruidDataSourceFactory.createDataSource(BeanUtils.pojo2Map(resource));
		} catch (Exception e) {
			e.printStackTrace();
		}

		return dataSource;
	}

	public static void main(String[] args) {
		Context context = XMLUtils.XMLToObject(Context.class, "WebContent/META-INF/context.xml");
		System.out.println(context.WatchedResource);
		System.out.println(context.Resource.get(0).factory);
	}

}
