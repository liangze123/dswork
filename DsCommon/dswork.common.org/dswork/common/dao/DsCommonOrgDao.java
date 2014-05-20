/**
 * 组织机构Dao
 */
package dswork.common.dao;

import java.util.HashMap;
import java.util.Map;
import org.springframework.stereotype.Repository;

import dswork.common.model.DsCommonOrg;
import dswork.core.db.BaseDao;

@Repository
@SuppressWarnings("unchecked")
public class DsCommonOrgDao extends BaseDao<DsCommonOrg, Long>
{
	@Override
	public Class getEntityClass()
	{
		return DsCommonOrg.class;
	}

	/**
	 * 移动节点
	 * @param id 组织机构主键
	 * @param pid 小于等于0则是根节点
	 * @param map 临时map对象，传递进来后会被clear，并放入id和seq
	 */
	public void updatePid(Long id, long pid, Map<String, Object> map)
	{
		map.clear();
		map.put("id", id);
		map.put("pid", pid);
		executeUpdate("updatePid", map);
	}

	/**
	 * 排序节点
	 * @param id 组织机构主键
	 * @param seq 排序位置
	 */
	public void updateSeq(Long id, Long seq)
	{
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("id", id);
		map.put("seq", seq);
		executeUpdate("updateSeq", map);
	}
}
