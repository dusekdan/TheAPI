package me.Straiker123;

import static java.util.Map.Entry.comparingByValue;
import static java.util.stream.Collectors.toMap;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;

public class RankingAPI {
	HashMap<Object, Double> s;
	public RankingAPI(HashMap<Object, Double> map) {
		s=map.entrySet().stream().sorted(comparingByValue()).collect(toMap(e -> e.getKey(),
				e -> e.getValue(), (e1, e2) -> e2,LinkedHashMap::new));
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public Object getObject(int position) {
		try {
		return new ArrayList(s.keySet()).get(position);
		}catch(Exception e) {
			//out
			return null;
		}
	}
	
	public double getValue(Object o) {
		double d = -1;
		if(s.containsKey(o))d=s.get(o);
		return d;
	}
	
	public int getPosition(Object o) {
		int i = 0;
		int result = -1;
		if(s.containsKey(o))
		for(Object get : s.keySet()) {
			++i;
			if(get.equals(o)) {
				result= i;
				break;
			}
		}
		return result;
	}
}