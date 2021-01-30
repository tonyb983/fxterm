package io.imtony.vdrive.fxterm.google.services

import com.google.api.services.calendar.Calendar

interface GoogleCalendarService : GoogleService<Calendar> {
  companion object {
    fun create(serviceInitializer: ServiceInitializer): GoogleCalendarService =
      GoogleCalendarServiceImpl(serviceInitializer)
  }
}

private class GoogleCalendarServiceImpl(serviceCreator: ServiceInitializer) : GoogleCalendarService,
  GenericInjectedService<Calendar>(
    lazy { serviceCreator.createCalendar() }
  )
