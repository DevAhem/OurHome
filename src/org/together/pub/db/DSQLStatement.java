package org.together.pub.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import javax.el.ELContext;
import javax.el.ELResolver;
import javax.el.ExpressionFactory;
import javax.el.FunctionMapper;
import javax.el.ValueExpression;
import javax.el.VariableMapper;

import org.apache.el.lang.ExpressionBuilder;

public class DSQLStatement extends ELContext {
	private static final ValueExpression valueExpression = ExpressionFactory
			.newInstance().createValueExpression("?", String.class);
	private final SQLParams params;
	private final List<ParamProp> tempParamList = new ArrayList<>();

	public DSQLStatement(SQLParams params) {
		this.params = params;
	}

	public static PreparedStatement create(Connection conn, String dsql,
			SQLParams params) {
		DSQLStatement ds = new DSQLStatement(params);
		ValueExpression ve = new ExpressionBuilder(dsql, ds)
				.createValueExpression(String.class);
		String finalSQL = String.valueOf(ve.getValue(ds));
		return ds.setParams(conn, finalSQL);
	}

	@Override
	public ELResolver getELResolver() {
		return null;
	}

	@Override
	public FunctionMapper getFunctionMapper() {
		return null;
	}

	@Override
	public VariableMapper getVariableMapper() {

		return new VariableMapper() {

			@Override
			public ValueExpression resolveVariable(String variable) {
				try {
					ParamProp paramProp = params.get(variable);
					tempParamList.add(paramProp);
				} catch (Exception e) {
					e.printStackTrace();
				}
				return valueExpression;
			}

			@Override
			public ValueExpression setVariable(String variable,
					ValueExpression expression) {
				return null;
			}
		};
	}

	private PreparedStatement setParams(Connection conn, String finalSQL) {
		LinkedHashMap<Integer,Object> tmpOutput = new LinkedHashMap<>();
		try {
			PreparedStatement pst = conn.prepareStatement(finalSQL);
			int idx = 1;
			for (ParamProp param : tempParamList) {
				Integer i = idx++;
				if (param.clazz != null) {
					DBUtils.getPstSetterMethod(param.clazz).invoke(pst, i,
							param.value);
					
					tmpOutput.put(i, param.value);
				} else {
					throw new RuntimeException("Param " + i
							+ " type should be null");
				}
			}

			
			
			
			System.out.println("params = " + tmpOutput + " finalSQL = "
					+ finalSQL);
			return pst;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public static void main(String[] args) {
		// ValueExpression ve = new ExpressionBuilder("aaaa${bar}",
		// elContext).createValueExpression(String.class);
		//
		// System.out.println(ve.getValue(elContext));
	}
}
