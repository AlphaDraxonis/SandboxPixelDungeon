/*
 * Pixel Dungeon
 * Copyright (C) 2012-2015 Oleg Dolya
 *
 * Shattered Pixel Dungeon
 * Copyright (C) 2014-2024 Evan Debenham
 *
 * Sandbox Pixel Dungeon
 * Copyright (C) 2023-2024 AlphaDraxonis
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>
 */

package com.shatteredpixel.shatteredpixeldungeon.editor.lua;

import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.editor.Copyable;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.SkeletonSprite;
import com.watabou.noosa.Image;
import com.watabou.utils.Reflection;

import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LuaScript implements Comparable<LuaScript>, Copyable<LuaScript> {

	public Class<?> type;
	public String desc;
	public String code;

	public final String pathFromRoot;

	public LuaScript(Class<?> type, String desc, String pathFromRoot) {
		this.type = type;
		this.desc = desc;
		this.pathFromRoot = pathFromRoot;
	}

	public static LuaScript readFromFileContent(String fileContent, String pathFromRoot) {
		fileContent = fileContent.replace("\r", "");
		String[] lines = fileContent.split("\n");

		if (lines.length < 3) return null;

		LuaScript luaScript = new LuaScript(
				Reflection.forName(lines[0].substring(2)),
				lines[1].replace((char) 29, '\n').substring(2),
				pathFromRoot
		);
		if (lines.length > 3) {
			int index = lines[0].length() + lines[1].length() + 3;
			luaScript.code = fileContent.substring(index);
		}
		return luaScript;
	}

	public String getAsFileContent() {
		StringBuilder b = new StringBuilder();

		b.append("--").append(type.getName()).append('\n');

		b.append("--").append(desc.replace('\n', (char) 29)).append('\n');

		b.append(code);

		return b.toString();
	}


	@Override
	public int compareTo(LuaScript o) {
		return pathFromRoot.compareTo(o.pathFromRoot);
	}

	//replaces comments and string values so the pattern matcher
	public static String cleanLuaCode(String luaCode) {
		luaCode = replaceBlockComments(luaCode);
		luaCode = replaceMatches(luaCode, "--.*");
		luaCode = replaceMatches(luaCode, "\"(?:[^\"\\\\]|\\\\.)*\"|'(?:[^'\\\\]|\\\\.)*'");
		return luaCode;
	}

	private static String replaceMatches(String input, String regex) {
		Pattern pattern = Pattern.compile(regex);
		Matcher matcher = pattern.matcher(input);
		StringBuffer result = new StringBuffer();
		while (matcher.find()) {
			matcher.appendReplacement(result, repeatString('_', matcher.group().length()));
		}
		matcher.appendTail(result);
		return result.toString();
	}

	private static String replaceBlockComments(String input) {
		Pattern pattern = Pattern.compile("--\\[\\[(?s)(.*?)\\]\\]");
		Matcher matcher = pattern.matcher(input);
		StringBuffer result = new StringBuffer();
		while (matcher.find()) {
			matcher.appendReplacement(result, repeatString('_', matcher.group().length()));
		}
		matcher.appendTail(result);
		return result.toString();
	}

	public static String extractMethodFromScript(String cleanedCode, String originalCode, String methodName) {
		String method = extractRawMethodFromScript(cleanedCode, originalCode, methodName);
		return method == null ? null : cutExtractedMethod(method);
	}

	public static String extractRawMethodFromScript(String cleanedCode, String originalCode, String methodName) {

		String patternString = "function\\s+" + methodName + "\\s*\\(";
		Pattern pattern = Pattern.compile(patternString, Pattern.DOTALL);
		Matcher matcher = pattern.matcher(cleanedCode);


		if (matcher.find()) {

			int outputStartIndex = matcher.start();
			int startIndex = matcher.end();

			Matcher startMatcher = Pattern.compile("\\b(if|for|while|repeat|function)\\b", Pattern.DOTALL).matcher(cleanedCode.substring(startIndex));
			Matcher endMatcher = Pattern.compile("\\bend\\b", Pattern.DOTALL).matcher(cleanedCode.substring(startIndex));

			LinkedList<Integer> starts = new LinkedList<>();
			LinkedList<Integer> ends = new LinkedList<>();

			while (startMatcher.find()) {
				starts.add(startMatcher.end());
			}
			while (endMatcher.find()) {
				ends.add(endMatcher.end());
			}

			if (starts.isEmpty() || ends.isEmpty()) {
				return ends.isEmpty() ? null : originalCode.substring(outputStartIndex, ends.getFirst() + startIndex);
			}

			int indexNextStart = starts.removeFirst();
			int indexNextEnd = ends.removeFirst();

			int nestedLevel = 0;//negative value mean we have left the function

			while (true) {
				if (indexNextStart < indexNextEnd) {
					nestedLevel++;
					indexNextStart = starts.isEmpty() ? Integer.MAX_VALUE : starts.removeFirst();
				} else {
					nestedLevel--;
					if (nestedLevel < 0) {
						return originalCode.substring(outputStartIndex, indexNextEnd + startIndex);
					}
					if (ends.isEmpty()) return originalCode.substring(outputStartIndex, indexNextEnd + startIndex);
					indexNextEnd = ends.removeFirst();
				}

			}

		}
		return null;
	}

	private static String cutExtractedMethod(String fullMethod) {
		String cutted = fullMethod.substring(fullMethod.indexOf(')') + 1, fullMethod.length() - 3);//remove function declaration and 'end';
		if (cutted.startsWith("\n")) cutted = cutted.substring(1);
		if (cutted.endsWith("\n")) cutted = cutted.substring(0, cutted.length() - 1);
		return cutted;
	}

	public static List<String> allFunctionNames(String cleanedCode) {
		List<String> functionNames = new ArrayList<>();
		Matcher finder = Pattern.compile("function\\s+([\\w_]+)\\s*\\(").matcher(cleanedCode);
		while (finder.find()) {
			functionNames.add(finder.group(1));
		}
		return functionNames;
	}

	public static String extractTableFromScript(String cleanedCode, String originalCode, String tableName, boolean onlyValues) {
		String regex = tableName + "\\s*=\\s*\\{";
		Pattern pattern = Pattern.compile(regex);
		Matcher matcher = pattern.matcher(cleanedCode);

		int tableNameEnd, tableNameStart;
		if (matcher.find()) {
			tableNameStart = matcher.start();
			tableNameEnd = matcher.end();
		} else {
			return null;
		}

		int nestingLevel = 1;
		int tableEndIndex = tableNameEnd - 1;
		int maxIndex = cleanedCode.length();
		while (tableEndIndex < maxIndex && nestingLevel > 0) {
			tableEndIndex++;
			char c = cleanedCode.charAt(tableEndIndex);
			if (c == '{') {
				nestingLevel++;
			} else if (c == '}') {
				nestingLevel--;
			}
		}

		if (nestingLevel != 0) {
			return null;
		}

		if (onlyValues) {
			String fullTable = originalCode.substring(tableNameEnd, tableEndIndex);
			if (fullTable.startsWith("\n")) fullTable = fullTable.substring(1);
			if (fullTable.endsWith("\n")) fullTable = fullTable.substring(0, fullTable.length() - 1);
			return fullTable;
		}
		return originalCode.substring(tableNameStart, tableEndIndex + 1);
	}

	private static String repeatString(char c, int count) {
		StringBuilder s = new StringBuilder();
		for (int i = 0; i < count; i++) {
			s.append(c);
		}
		return s.toString();
	}

	@Override
	public LuaScript getCopy() {
		LuaScript copy = new LuaScript(type, desc, pathFromRoot);
		copy.code = code;
		return copy;
	}

	public Image sprite() {
		boolean abstrct = Modifier.isAbstract(type.getModifiers());
		if (Mob.class.isAssignableFrom(type)) return abstrct ? new SkeletonSprite() : ((Mob) Reflection.newInstance(type)).sprite();
		return new ItemSprite();
	}
}