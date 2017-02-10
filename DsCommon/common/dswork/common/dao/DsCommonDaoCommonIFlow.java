/**
 * 公共Dao
 */
package dswork.common.dao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.stereotype.Repository;

import dswork.common.model.IFlow;
import dswork.common.model.IFlowPi;
import dswork.common.model.IFlowPiData;
import dswork.common.model.IFlowTask;
import dswork.common.model.IFlowWaiting;
import dswork.core.db.MyBatisDao;
import dswork.core.util.TimeUtil;
import dswork.core.util.UniqueId;

@Repository
@SuppressWarnings("all")
public class DsCommonDaoCommonIFlow extends MyBatisDao
{
	private SqlSessionTemplate sqlSessionTemplateCommon;

	@Override
	protected SqlSessionTemplate getSqlSessionTemplate()
	{
		if(sqlSessionTemplateCommon == null)
		{
			return super.getSqlSessionTemplate();
		}
		return sqlSessionTemplateCommon;
	}
	
	public void setSqlSessionTemplateCommon(SqlSessionTemplate sqlSessionTemplate)
	{
		this.sqlSessionTemplateCommon = sqlSessionTemplate;
	}
	
	@Override
	protected Class getEntityClass()
	{
		return DsCommonDaoCommonIFlow.class;
	}
	protected IFlow getFlow(String alias)
	{
		IFlow flow = (IFlow) executeSelect("selectFlow", alias);
		return flow;
	}
	protected IFlowTask getFlowTask(Long flowid, String talias)
	{
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("flowid", flowid);
		map.put("talias", talias);
		return (IFlowTask) executeSelect("selectFlowTask", map);
	}
	protected List<IFlowTask> queryFlowTask(Long flowid)
	{
		return executeSelectList("queryFlowTask", flowid);
	}
}
