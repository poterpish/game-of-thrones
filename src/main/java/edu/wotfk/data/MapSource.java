package edu.wotfk.data;

import edu.wotfk.domain.WorldMap;
/** Target interface used by the campaign; no legacy rows escape this boundary. */
public interface MapSource { WorldMap load(); }
