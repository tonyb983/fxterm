package io.imtony.vdrive.fxterm.google.services

import com.google.api.services.calendar.Calendar

/**
 * The Google [Calendar] service interface.
 */
interface GoogleCalendarService : GoogleService<Calendar> {
  companion object {

    /**
     * Constructor function for a [GoogleCalendarService].
     */
    fun create(serviceInitializer: ServiceInitializer): GoogleCalendarService =
      GoogleCalendarServiceImpl(serviceInitializer)
  }
}

private class GoogleCalendarServiceImpl(serviceCreator: ServiceInitializer) : GoogleCalendarService,
  GenericService<Calendar>(
    lazy { serviceCreator.createCalendar() }
  )
