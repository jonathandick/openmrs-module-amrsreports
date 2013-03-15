package org.openmrs.module.amrsreports.service;

import org.openmrs.Location;
import org.openmrs.module.amrsreports.MOHFacility;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;

/**
 * Service for dealing with facilities
 */
public interface MOHFacilityService {

	@Transactional(readOnly = true)
	public List<MOHFacility> getAllFacilities();

	@Transactional(readOnly = true)
	public List<MOHFacility> getAllFacilities(Boolean includeRetired);

	@Transactional(readOnly = true)
	public MOHFacility getFacility(Integer facilityId);

	@Transactional
	public MOHFacility saveFacility(MOHFacility facility);

	@Transactional(readOnly = true)
	public Set<Location> getUnallocatedLocations();

	@Transactional(readOnly = true)
	public Set<Location> getAllocatedLocations();

	@Transactional
	void retireFacility(MOHFacility facility, String retireReason);

	@Transactional
	void purgeFacility(MOHFacility facility);
}
