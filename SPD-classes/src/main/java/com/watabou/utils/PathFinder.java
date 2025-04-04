/*
 * Pixel Dungeon
 * Copyright (C) 2012-2015 Oleg Dolya
 *
 * Shattered Pixel Dungeon
 * Copyright (C) 2014-2025 Evan Debenham
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

package com.watabou.utils;

import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

public class PathFinder {
	
	public static int[] distance;
	private static int[] maxVal;
	
	private static boolean[] goals;
	private static int[] queue;
	private static boolean[] queued; //currently only used in getStepBack, other can piggyback on distance
	
	private static int size = 0;
	private static int width = 0;

	private static int[] dir;
	private static int[] dirLR;

	//performance-light shortcuts for some common pathfinder cases
	//they are in array-access order for increased memory performance
	public static int[] NEIGHBOURS4;
	public static int[] NEIGHBOURS8;
	public static int[] NEIGHBOURS9;

	//similar to their equivalent neighbour arrays, but the order is clockwise.
	//Useful for some logic functions, but is slower due to lack of array-access order.
	public static int[] CIRCLE4;
	public static int[] CIRCLE8;
	
	public static void setMapSize( int width, int height ) {
		
		PathFinder.width = width;
		PathFinder.size = width * height;
		
		distance = new int[size];
		goals = new boolean[size];
		queue = new int[size];
		queued = new boolean[size];

		maxVal = new int[size];
		Arrays.fill(maxVal, Integer.MAX_VALUE);

		dir = new int[]{-1, +1, -width, +width, -width-1, -width+1, +width-1, +width+1};
		dirLR = new int[]{-1-width, -1, -1+width, -width, +width, +1-width, +1, +1+width};

		NEIGHBOURS4 = new int[]{-width, -1, +1, +width};
		NEIGHBOURS8 = new int[]{-width-1, -width, -width+1, -1, +1, +width-1, +width, +width+1};
		NEIGHBOURS9 = new int[]{-width-1, -width, -width+1, -1, 0, +1, +width-1, +width, +width+1};

		CIRCLE4 = new int[]{-width, +1, +width, -1};
		CIRCLE8 = new int[]{-width-1, -width, -width+1, +1, +width+1, +width, +width-1, -1};

		cellRadiusCache.clear();
	}

	public static Path find( int from, int to, boolean[] passable, Object ch ) {

		if (!buildDistanceMap( from, to, passable, ch )) {
			return null;
		}
		
		Path result = new Path();
		int s = from;

		// From the starting position we are moving downwards,
		// until we reach the ending point
		do {
			int minD = distance[s];
			int mins = s;
			
			for (int i=0; i < dir.length; i++) {
				
				int n = s + dir[i];
				
				int thisD = distance[n];
				if (thisD < minD && ArrowCellInterface.allowsStep(s, n, ch)) {
					minD = thisD;
					mins = n;
				}
			}
			s = mins;
			result.add( s );
		} while (s != to);
		
		return result;
	}
	
	public static int getStep( int from, int to, boolean[] passable, Object ch ) {
		
		if (!buildDistanceMap( from, to, passable, ch )) {
			return -1;
		}
		
		// From the starting position we are making one step downwards
		int minD = distance[from];
		int best = from;
		
		int step, stepD;
		
		for (int i=0; i < dir.length; i++) {

			if ((stepD = distance[step = from + dir[i]]) < minD) {
				minD = stepD;
				best = step;
			}
		}

		return best;
	}
	
	public static int getStepBack( int cur, int from, int lookahead, boolean[] passable, boolean canApproachFromPos, Object ch ) {

		int d = buildEscapeDistanceMap( cur, from, lookahead, passable, ch );
		if (d == 0) return -1;

		if (!canApproachFromPos) {
			//We can't approach the position we are retreating from
			//re-calculate based on this, and reduce the target distance if need-be
			int head = 0;
			int tail = 0;

			int newD = distance[cur];
			BArray.setFalse(queued);

			queue[tail++] = cur;
			queued[cur] = true;

			while (head < tail) {
				int step = queue[head++];

				if (distance[step] > newD) {
					newD = distance[step];
				}

				ArrowCellInterface arrowCellFrom = getArrowCellAt.apply(step);

				int start = (step % width == 0 ? 3 : 0);
				int end = ((step + 1) % width == 0 ? 3 : 0);
				for (int i = start; i < dirLR.length - end; i++) {

					if (arrowCellFrom != null && !arrowCellFrom.allowsDirectionEnter(dirLR[i], ch)) continue;

					int n = step + dirLR[i];

					ArrowCellInterface arrowCell = getArrowCellAt.apply(n);
					if (arrowCell != null && !arrowCell.allowsDirectionLeaving(-dirLR[i], ch)) continue;

					if (n >= 0 && n < size && passable[n]) {
						if (distance[n] < distance[cur]) {
							passable[n] = false;
						} else if (distance[n] >= distance[step] && !queued[n]) {
							// Add to queue
							queue[tail++] = n;
							queued[n] = true;
						}
					}
				}

			}

			d = Math.min(newD, d);
		}

		for (int i=0; i < size; i++) {
			goals[i] = distance[i] == d;
		}
		if (!buildDistanceMap( cur, goals, passable, ch )) {
			return -1;
		}

		int s = cur;
		
		// From the starting position we are making one step downwards
		int minD = distance[s];
		int mins = s;
		
		for (int i=0; i < dir.length; i++) {

			int n = s + dir[i];
			int thisD = distance[n];
			
			if (thisD < minD && ArrowCellInterface.allowsStep(s, n, ch)) {
				minD = thisD;
				mins = n;
			}
		}

		return mins;
	}

	public static Function<Integer, ArrowCellInterface> getArrowCellAt;
	
	private static boolean buildDistanceMap( int from, int to, boolean[] passable, Object ch ) {
		
		if (from == to) {
			return false;
		}

		System.arraycopy(maxVal, 0, distance, 0, maxVal.length);
		
		boolean pathFound = false;
		
		int head = 0;
		int tail = 0;
		
		// Add to queue
		queue[tail++] = to;
		distance[to] = 0;
		
		while (head < tail) {
			
			// Remove from queue
			int step = queue[head++];
			if (step == from) {
				pathFound = true;
				break;
			}
			int nextDistance = distance[step] + 1;

			ArrowCellInterface arrowCellFrom = getArrowCellAt.apply(step);
			
			int start = (step % width == 0 ? 3 : 0);
			int end   = ((step+1) % width == 0 ? 3 : 0);
			for (int i = start; i < dirLR.length - end; i++) {

				if (arrowCellFrom != null && !arrowCellFrom.allowsDirectionEnter(dirLR[i], ch)) continue;

				int n = step + dirLR[i];
				if (n == from || (n >= 0 && n < size && passable[n] && (distance[n] > nextDistance))) {
					ArrowCellInterface arrowCell = getArrowCellAt.apply(n);
					if (arrowCell != null && !arrowCell.allowsDirectionLeaving(-dirLR[i], ch)) continue;
					// Add to queue
					queue[tail++] = n;
					distance[n] = nextDistance;
				}
					
			}
		}
		
		return pathFound;
	}
	
	public static void buildDistanceMapForCharacters(int to, boolean[] passable, int limit, Object ch ) {
		buildDistanceMapForEnvironmentals(to, passable, limit, false, ch);
	}

	public static void buildDistanceMapForEnvironmentals(int to, boolean[] passable, int limit ) {
		buildDistanceMapForEnvironmentals(to, passable, limit, true, null);
	}
	
	private static void buildDistanceMapForEnvironmentals(int to, boolean[] passable, int limit, boolean ignoreArrowCells, Object ch ) {

		System.arraycopy(maxVal, 0, distance, 0, maxVal.length);
		
		int head = 0;
		int tail = 0;
		
		// Add to queue
		queue[tail++] = to;
		distance[to] = 0;
		
		while (head < tail) {
			
			// Remove from queue
			int step = queue[head++];
			
			int nextDistance = distance[step] + 1;
			if (nextDistance > limit) {
				return;
			}

			ArrowCellInterface arrowCellFrom = ignoreArrowCells ? null : getArrowCellAt.apply(step);
			
			int start = (step % width == 0 ? 3 : 0);
			int end   = ((step+1) % width == 0 ? 3 : 0);
			for (int i = start; i < dirLR.length - end; i++) {

				if (arrowCellFrom != null && !arrowCellFrom.allowsDirectionEnter(dirLR[i], ch)) continue;

				int n = step + dirLR[i];
				if (n >= 0 && n < size && passable[n] && (distance[n] > nextDistance)) {
					ArrowCellInterface arrowCell = ignoreArrowCells ? null : getArrowCellAt.apply(n);
					if (arrowCell != null && !arrowCell.allowsDirectionLeaving(-dirLR[i], ch)) continue;
					// Add to queue
					queue[tail++] = n;
					distance[n] = nextDistance;
				}
					
			}
		}
	}
	
	private static boolean buildDistanceMap( int from, boolean[] to, boolean[] passable, Object ch ) {
		
		if (to[from]) {
			return false;
		}
		
		System.arraycopy(maxVal, 0, distance, 0, maxVal.length);
		
		boolean pathFound = false;
		
		int head = 0;
		int tail = 0;
		
		// Add to queue
		for (int i=0; i < size; i++) {
			if (to[i]) {
				queue[tail++] = i;
				distance[i] = 0;
			}
		}
		
		while (head < tail) {
			
			// Remove from queue
			int step = queue[head++];
			if (step == from) {
				pathFound = true;
				break;
			}
			int nextDistance = distance[step] + 1;

			ArrowCellInterface arrowCellFrom = getArrowCellAt.apply(step);
			
			int start = (step % width == 0 ? 3 : 0);
			int end   = ((step+1) % width == 0 ? 3 : 0);
			for (int i = start; i < dirLR.length - end; i++) {

				if (arrowCellFrom != null && !arrowCellFrom.allowsDirectionEnter(dirLR[i], ch)) continue;

				int n = step + dirLR[i];
				if (n == from || (n >= 0 && n < size && passable[n] && (distance[n] > nextDistance))) {
					ArrowCellInterface arrowCell = getArrowCellAt.apply(n);
					if (arrowCell != null && !arrowCell.allowsDirectionLeaving(-dirLR[i], ch)) continue;
					// Add to queue
					queue[tail++] = n;
					distance[n] = nextDistance;
				}
					
			}
		}
		
		return pathFound;
	}

	//the lookahead is the target number of cells to retreat toward from our current position's
	// distance from the position we are escaping from. Returns the highest found distance, up to the lookahead
	private static int buildEscapeDistanceMap( int cur, int from, int lookAhead, boolean[] passable, Object ch ) {
		
		System.arraycopy(maxVal, 0, distance, 0, maxVal.length);
		
		int destDist = Integer.MAX_VALUE;
		
		int head = 0;
		int tail = 0;
		
		// Add to queue
		queue[tail++] = from;
		distance[from] = 0;
		
		int dist = 0;
		
		while (head < tail) {
			
			// Remove from queue
			int step = queue[head++];
			dist = distance[step];

			ArrowCellInterface arrowCellFrom = getArrowCellAt.apply(step);
			
			if (dist > destDist) {
				return destDist;
			}
			
			if (step == cur) {
				destDist = dist + lookAhead;
			}
			
			int nextDistance = dist + 1;
			
			int start = (step % width == 0 ? 3 : 0);
			int end   = ((step+1) % width == 0 ? 3 : 0);
			for (int i = start; i < dirLR.length - end; i++) {

				if (arrowCellFrom != null && !arrowCellFrom.allowsDirectionEnter(dirLR[i], ch)) continue;

				int n = step + dirLR[i];
				if (n >= 0 && n < size && passable[n] && distance[n] > nextDistance) {
					ArrowCellInterface arrowCell = getArrowCellAt.apply(n);
					if (arrowCell != null && !arrowCell.allowsDirectionLeaving(-dirLR[i], ch)) continue;
					// Add to queue
					queue[tail++] = n;
					distance[n] = nextDistance;
				}
					
			}
		}
		
		return dist;
	}
	
	public static void buildDistanceMap( int to, boolean[] passable, Object ch ) {
		
		System.arraycopy(maxVal, 0, distance, 0, maxVal.length);
		
		int head = 0;
		int tail = 0;
		
		// Add to queue
		queue[tail++] = to;
		distance[to] = 0;
		
		while (head < tail) {
			
			// Remove from queue
			int step = queue[head++];
			int nextDistance = distance[step] + 1;

			ArrowCellInterface arrowCellFrom = getArrowCellAt.apply(step);
			
			int start = (step % width == 0 ? 3 : 0);
			int end   = ((step+1) % width == 0 ? 3 : 0);
			for (int i = start; i < dirLR.length - end; i++) {

				if (arrowCellFrom != null && !arrowCellFrom.allowsDirectionEnter(dirLR[i], ch)) continue;

				int n = step + dirLR[i];
				if (n >= 0 && n < size && passable[n] && (distance[n] > nextDistance)) {
					ArrowCellInterface arrowCell = getArrowCellAt.apply(n);
					if (arrowCell != null && !arrowCell.allowsDirectionLeaving(-dirLR[i], ch)) continue;
					// Add to queue
					queue[tail++] = n;
					distance[n] = nextDistance;
				}
					
			}
		}
	}

	private static final Map<Integer, int[]> cellRadiusCache = new HashMap<>(5);
	public static int[] GET_ALL_CELLS_IN_RADIUS(int radius){
		if (radius == 1) return PathFinder.NEIGHBOURS9;
		if (radius == 0) return new int[]{0};
		if (cellRadiusCache.containsKey(radius)) return cellRadiusCache.get(radius);
		int sideLength = 2*radius+1;
		int[] result = new int[sideLength * sideLength];
		int posX = 0, posY = 0;
		for (int i = 0; i < result.length; i++) {
			result[i] = posX-radius + (posY-radius)*width;
			posX++;
			if (posX == sideLength) {
				posY++;
				posX = 0;
			}
		}
		cellRadiusCache.put(radius, result);
		return result;
	}
	
	@SuppressWarnings("serial")
	public static class Path extends LinkedList<Integer> {
	}

	public interface ArrowCellInterface {
		boolean allowsDirectionLeaving(int pathfinderNeighboursValue, Object ch);
		boolean allowsDirectionEnter(int pathfinderNeighboursValue, Object ch);

		/**
		 * Assumes from and to are <b><u>adjacent</u></b>!
		 */
		static boolean allowsStep(int from, int to, Object ch) {
			//if (!adjacent) return false;
			ArrowCellInterface acLeave = getArrowCellAt.apply(from);
			ArrowCellInterface acEnter = getArrowCellAt.apply(to);
			int direction = to - from;
			return (acLeave == null || acLeave.allowsDirectionLeaving(direction, ch))
					&& (acEnter == null || acEnter.allowsDirectionEnter(-direction, ch));
		}
	}

}
