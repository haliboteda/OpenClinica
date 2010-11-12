package org.akaza.openclinica.dao.hibernate;

import org.akaza.openclinica.dao.core.CoreResources;
import org.akaza.openclinica.domain.rule.RuleBean;
import org.akaza.openclinica.domain.rule.RuleSetBean;
import org.akaza.openclinica.domain.rule.RuleSetRuleBean;

import java.util.ArrayList;

public class RuleSetRuleDao extends AbstractDomainDao<RuleSetRuleBean> {

    private CoreResources coreResources;

    @Override
    public Class<RuleSetRuleBean> domainClass() {
        return RuleSetRuleBean.class;
    }

    @SuppressWarnings("unchecked")
    public ArrayList<RuleSetRuleBean> findByRuleSetBeanAndRuleBean(RuleSetBean ruleSetBean, RuleBean ruleBean) {
        String query = "from " + getDomainClassName() + " ruleSetRule  where ruleSetRule.ruleSetBean = :ruleSetBean" + " AND ruleSetRule.ruleBean = :ruleBean ";
        org.hibernate.Query q = getCurrentSession().createQuery(query);
        q.setParameter("ruleSetBean", ruleSetBean);
        q.setParameter("ruleBean", ruleBean);
        return (ArrayList<RuleSetRuleBean>) q.list();
    }
    
    @SuppressWarnings("unchecked")
    public ArrayList<RuleSetRuleBean> findByRuleSetStudyIdAndStatusAvail(Integer studyId) {
        String query = "from " + getDomainClassName() + " ruleSetRule  where ruleSetRule.ruleSetBean.studyId = :studyId and status = :status ";
        org.hibernate.Query q = getCurrentSession().createQuery(query);
        q.setInteger("studyId", studyId);
        q.setParameter("status", org.akaza.openclinica.domain.Status.AVAILABLE);
        return (ArrayList<RuleSetRuleBean>) q.list();
    }

    public int getCountWithFilter(final ViewRuleAssignmentFilter filter) {

        // Using a sql query because we are referencing objects not managed by hibernate
        String query =
            "select COUNT(DISTINCT(rsr.id)) from rule_set_rule rsr " + " join rule_set rs on rs.id = rsr.rule_set_id "
                + " left outer join study_event_definition sed on rs.study_event_definition_id = sed.study_event_definition_id "
                + " left outer join crf_version cv on rs.crf_version_id = cv.crf_version_id " + " left outer join crf c on rs.crf_id = c.crf_id "
                + " left outer join item i on rs.item_id = i.item_id " + " left outer join item_group ig on rs.item_group_id = ig.item_group_id "
                + " join rule_expression re on rs.rule_expression_id = re.id " + " join rule r on r.id = rsr.rule_id "
                + " join rule_expression rer on r.rule_expression_id = rer.id " + " join rule_action ra on ra.rule_set_rule_id = rsr.id " + " where ";

        query += filter.execute("");
        org.hibernate.Query q = getCurrentSession().createSQLQuery(query);

        return ((Number) q.uniqueResult()).intValue();
    }

    @SuppressWarnings("unchecked")
    public ArrayList<RuleSetRuleBean> getWithFilterAndSort(final ViewRuleAssignmentFilter filter, final ViewRuleAssignmentSort sort, final int rowStart,
            final int rowEnd) {

        String select =
            "select DISTINCT(rsr.id),rsr.rule_set_id,rsr.rule_id,rsr.owner_id,rsr.date_created, rsr.date_updated, rsr.update_id, rsr.status_id,rsr.version,i.name as iname,re.value as revalue,sed.name as sedname,c.name as cname,cv.name as cvname,ig.name as igname,rer.value as rervalue,r.oc_oid as rocoid,r.description as rdescription,r.name as rname from rule_set_rule rsr ";
        if ("oracle".equalsIgnoreCase(CoreResources.getDBName())) {
            select =
                "select DISTINCT(rsr.id),rsr.rule_set_id,rsr.rule_id,rsr.owner_id,rsr.date_created, rsr.date_updated, rsr.update_id, rsr.status_id,rsr.version,i.name iname,re.value revalue,sed.name sedname,c.name cname,cv.name cvname,ig.name igname,rer.value rervalue,r.oc_oid rocoid,r.description rdescription,r.name rname from rule_set_rule rsr ";
        }

        String query =
            select + " join rule_set rs on rs.id = rsr.rule_set_id "
                + " left outer join study_event_definition sed on rs.study_event_definition_id = sed.study_event_definition_id "
                + " left outer join crf_version cv on rs.crf_version_id = cv.crf_version_id " + " left outer join crf c on rs.crf_id = c.crf_id "
                + " left outer join item i on rs.item_id = i.item_id " + " left outer join item_group ig on rs.item_group_id = ig.item_group_id "
                + " join rule_expression re on rs.rule_expression_id = re.id " + " join rule r on r.id = rsr.rule_id "
                + " join rule_expression rer on r.rule_expression_id = rer.id " + " join rule_action ra on ra.rule_set_rule_id = rsr.id " + " where ";

        query += filter.execute("");
        query += sort.execute("");
        org.hibernate.Query q = getCurrentSession().createSQLQuery(query).addEntity(domainClass());
        q.setFirstResult(rowStart);
        q.setMaxResults(rowEnd - rowStart);
        return (ArrayList<RuleSetRuleBean>) q.list();
    }

    public CoreResources getCoreResources() {
        return coreResources;
    }

    public void setCoreResources(CoreResources coreResources) {
        this.coreResources = coreResources;
    }
}
