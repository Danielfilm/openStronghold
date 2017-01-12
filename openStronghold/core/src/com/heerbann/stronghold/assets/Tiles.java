package com.heerbann.stronghold.assets;

import com.badlogic.gdx.utils.reflect.ClassReflection;
import com.badlogic.gdx.utils.reflect.Field;
import com.badlogic.gdx.utils.reflect.ReflectionException;

public class Tiles {

	public static class Tile_Castle_gm1{
		public static int gm1_01;
		public static int gm1_02;
		public static int gm1_03;
		public static int gm1_04;
		public static int gm1_05;
		public static int gm1_06;
		public static int gm1_07;
		public static int gm1_08;
		public static int gm1_09;
		public static int gm1_10;
		public static int gm1_11;
		public static int gm1_12;
		public static int gm1_13;
		public static int gm1_14;
		public static int gm1_15;
		public static int gm1_16;
		public static int gm1_17;
		public static int gm1_18;
		public static int gm1_19;
		public static int gm1_20;
		public static int gm1_21;
		public static int gm1_22;
		public static int gm1_23;
		public static int gm1_24;
		public static int gm1_25;
		public static int gm1_26;
		public static int gm1_27;
		public static int gm1_28;
		public static int gm1_29;
		public static int gm1_30;
		public static int gm1_31;
		public static int gm1_32;
		public static int gm1_33;
		public static int gm1_34;
		public static int gm1_35;
		public static int gm1_36;
		public static int gm1_37;
		public static int gm1_38;
		public static int gm1_39;
		public static int gm1_40;
		public static int gm1_41;
		public static int gm1_42;
		public static int gm1_43;
		public static int gm1_44;
		public static int gm1_45;
		public static int gm1_46;
		public static int gm1_47;
		public static int gm1_48;
		public static int gm1_49;
		public static int gm1_50;
		public static int gm1_51;
		public static int gm1_52;
		public static int gm1_53;
		public static int gm1_54;
		public static int gm1_55;
		public static int gm1_56;
		public static int gm1_57;
		public static int gm1_58;
		public static int gm1_59;
		public static int gm1_60;
		public static int gm1_61;
		public static int gm1_62;
		public static int gm1_63;
		public static int gm1_64;
		public static int gm1_65;
		public static int gm1_66;
		public static int gm1_67;
		public static int gm1_68;
		public static int gm1_69;
		public static int gm1_70;
		public static int gm1_71;
		public static int gm1_72;
		public static int gm1_73;
		public static int gm1_74;
		public static int gm1_75;
		public static int gm1_76;
		public static int gm1_77;
		public static int gm1_78;
		public static int gm1_79;
		public static int gm1_80;
		public static int gm1_81;
		public static int gm1_82;
		public static int gm1_83;
		public static int gm1_84;
		public static int gm1_85;
		public static int gm1_86;
		public static int gm1_87;
		public static int gm1_88;
		public static int gm1_89;
		public static int gm1_90;
		public static int gm1_91;
		public static int gm1_92;
		public static int gm1_93;
		public static int gm1_94;
		public static int gm1_95;
		public static int gm1_96;
		public static int gm1_97;
		public static int gm1_98;
		
		static{
			Field[] fields = ClassReflection.getDeclaredFields(Tile_Castle_gm1.class);
			for(int i = 0; i < fields.length; i++){
				try {
					fields[i].set(Integer.class, i);
				} catch (ReflectionException e) {
					e.printStackTrace();
				}
			}
		}
		
		public static String[] getAsStringArray(int start, int end){
			if(start > end || start < 0) throw new IllegalStateException("start cant be higher than end");
			String[] a = new String[end - start];
			Field[] fields = ClassReflection.getDeclaredFields(Tile_Castle_gm1.class);
			if(end >= fields.length - 1) throw new IllegalStateException("end too high");
			int k = 0;
			for(int i = start; i <= end; i++){
				a[k] = "tile_castle.gm1/tile_castle." + fields[i].getName();
				k++;
			}
			return a;
		}
		
		public static String getForIndex(int index){
			Field[] fields = ClassReflection.getDeclaredFields(Tile_Castle_gm1.class);
			if(index >= fields.length - 1 || index < 0) throw new IllegalStateException("index out of bounds");
			return "tile_castle.gm1/tile_castle." + fields[index].getName();
		}

	}
}
