package uia.utils.dao;

import org.junit.Assert;
import org.junit.Test;

public class CamelNamingTest {

	@Test
	public void test() {
		Assert.assertEquals("first", CamelNaming.lower("fIrst"));
		Assert.assertEquals("firstName", CamelNaming.lower("firsT_nAME"));
		Assert.assertEquals("aJob", CamelNaming.lower("a_job"));
		Assert.assertEquals("beAGoodBoy", CamelNaming.lower("be_a_GOOD_boy"));

		Assert.assertEquals("First", CamelNaming.upper("firST"));
		Assert.assertEquals("FirstName", CamelNaming.upper("firsT_NaMe"));
		Assert.assertEquals("AJob", CamelNaming.upper("a_JOB"));
		Assert.assertEquals("BeAGoodBoy", CamelNaming.upper("BE_A_GOOD_BOY"));
}
}
