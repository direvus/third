#include <windows.h>
#include "include/thir.h"

const char class[] = "primary";

LRESULT CALLBACK proc(HWND w, UINT msg, WPARAM wp, LPARAM lp)
{
  switch(msg)
  {
    case WM_CREATE:
      {
	HWND b;
	HBITMAP i;

	b = CreateWindowEx(WS_EX_LEFT, "BUTTON", "d2", WS_CHILD | WS_VISIBLE | BS_PUSHBUTTON | BS_BITMAP | BS_FLAT,
	  10, 10, 57, 48,
	  w, (HMENU) IDC_D2, GetModuleHandle(NULL), NULL);

	i = LoadImage(GetModuleHandle(NULL), MAKEINTRESOURCE(IDB_D2), IMAGE_BITMAP,
	  0, 0, 0);

	SendMessage(b, BM_SETIMAGE, (WPARAM) IMAGE_BITMAP, (LPARAM) i);
      }
      {
	HWND b;
	HBITMAP i;

	b = CreateWindowEx(WS_EX_LEFT, "BUTTON", "d4", WS_CHILD | WS_VISIBLE | BS_PUSHBUTTON | BS_BITMAP | BS_FLAT,
	  67, 10, 57, 48,
	  w, (HMENU) IDC_D4, GetModuleHandle(NULL), NULL);

	i = LoadImage(GetModuleHandle(NULL), MAKEINTRESOURCE(IDB_D4), IMAGE_BITMAP,
	  0, 0, 0);

	SendMessage(b, BM_SETIMAGE, (WPARAM) IMAGE_BITMAP, (LPARAM) i);
      }
      {
	HWND b;
	HBITMAP i;

	b = CreateWindowEx(WS_EX_LEFT, "BUTTON", "d6", WS_CHILD | WS_VISIBLE | BS_PUSHBUTTON | BS_BITMAP,
	  124, 10, 57, 48,
	  w, (HMENU) IDC_D6, GetModuleHandle(NULL), NULL);

	/*
	i = LoadImage(GetModuleHandle(NULL), MAKEINTRESOURCE(IDB_D6), IMAGE_BITMAP,
	  0, 0, LR_LOADMAP3DCOLORS);

	SendMessage(b, BM_SETIMAGE, (WPARAM) IMAGE_BITMAP, (LPARAM) i);
	*/
      }
      {
	HWND b;
	HBITMAP i;

	b = CreateWindowEx(WS_EX_LEFT, "BUTTON", "d8", WS_CHILD | WS_VISIBLE | BS_PUSHBUTTON | BS_BITMAP,
	  181, 10, 57, 48,
	  w, (HMENU) IDC_D8, GetModuleHandle(NULL), NULL);

	/*
	i = LoadImage(GetModuleHandle(NULL), MAKEINTRESOURCE(IDB_D8), IMAGE_BITMAP,
	  0, 0, LR_LOADMAP3DCOLORS);

	SendMessage(b, BM_SETIMAGE, (WPARAM) IMAGE_BITMAP, (LPARAM) i);
	*/
      }
      {
	HWND b;
	HBITMAP i;

	b = CreateWindowEx(WS_EX_LEFT, "BUTTON", "d10", WS_CHILD | WS_VISIBLE | BS_PUSHBUTTON | BS_BITMAP,
	  238, 10, 57, 48,
	  w, (HMENU) IDC_D10, GetModuleHandle(NULL), NULL);

	/*
	i = LoadImage(GetModuleHandle(NULL), MAKEINTRESOURCE(IDB_D10), IMAGE_BITMAP,
	  0, 0, LR_LOADMAP3DCOLORS);

	SendMessage(b, BM_SETIMAGE, (WPARAM) IMAGE_BITMAP, (LPARAM) i);
	*/
      }
      {
	HWND b;
	HBITMAP i;

	b = CreateWindowEx(WS_EX_LEFT, "BUTTON", "d12", WS_CHILD | WS_VISIBLE | BS_PUSHBUTTON | BS_BITMAP,
	  295, 10, 57, 48,
	  w, (HMENU) IDC_D12, GetModuleHandle(NULL), NULL);

	/*
	i = LoadImage(GetModuleHandle(NULL), MAKEINTRESOURCE(IDB_D12), IMAGE_BITMAP,
	  0, 0, LR_LOADMAP3DCOLORS);

	SendMessage(b, BM_SETIMAGE, (WPARAM) IMAGE_BITMAP, (LPARAM) i);
	*/
      }
      {
	HWND b;
	HBITMAP i;

	b = CreateWindowEx(WS_EX_LEFT, "BUTTON", "d20", WS_CHILD | WS_VISIBLE | BS_PUSHBUTTON | BS_BITMAP,
	  352, 10, 57, 48,
	  w, (HMENU) IDC_D20, GetModuleHandle(NULL), NULL);

	/*
	i = LoadImage(GetModuleHandle(NULL), MAKEINTRESOURCE(IDB_D20), IMAGE_BITMAP,
	  0, 0, LR_LOADMAP3DCOLORS);

	SendMessage(b, BM_SETIMAGE, (WPARAM) IMAGE_BITMAP, (LPARAM) i);
	*/
      }
      {
	HWND b;
	HBITMAP i;

	b = CreateWindowEx(WS_EX_LEFT, "BUTTON", "d100", WS_CHILD | WS_VISIBLE | BS_PUSHBUTTON | BS_BITMAP,
	  409, 10, 57, 48,
	  w, (HMENU) IDC_D100, GetModuleHandle(NULL), NULL);

	/*
	i = LoadImage(GetModuleHandle(NULL), MAKEINTRESOURCE(IDB_D100), IMAGE_BITMAP,
	  0, 0, LR_LOADMAP3DCOLORS);

	SendMessage(b, BM_SETIMAGE, (WPARAM) IMAGE_BITMAP, (LPARAM) i);
	*/
      }
      break;

    case WM_CLOSE:

      DestroyWindow(w);
      break;

    case WM_DESTROY:

      PostQuitMessage(0);
      break;

    default:

      return DefWindowProc(w, msg, wp, lp);
  }

  return 0;
}

int WINAPI WinMain (HINSTANCE inst, HINSTANCE prev_inst, PSTR opts, int show) 
{
  WNDCLASSEX wc;
  HWND w;
  MSG msg;

  wc.lpszClassName	= class;
  wc.lpfnWndProc	= proc;
  wc.hInstance		= inst;
  wc.cbSize		= sizeof(WNDCLASSEX);
  wc.cbClsExtra		= 0;
  wc.cbWndExtra		= 0;
  wc.hIcon		= NULL;
  wc.hIconSm		= NULL;
  wc.hbrBackground	= (HBRUSH) (COLOR_BTNFACE + 1);
  wc.hCursor		= LoadCursor(NULL, IDC_ARROW);
  wc.lpszMenuName	= NULL;
  wc.style		= 0;

  if(!RegisterClassEx(&wc))
  {
    MessageBox(NULL, "Window class registration failed", "Flagrant System Error", MB_ICONEXCLAMATION | MB_OK);
    return 0;
  }

  w = CreateWindowEx(WS_EX_WINDOWEDGE, class, "THIR", WS_OVERLAPPEDWINDOW | WS_BORDER, 
    CW_USEDEFAULT, CW_USEDEFAULT, 500, 300,
    NULL, NULL, inst, NULL);

  if(w == NULL)
  {
    MessageBox(NULL, "Window creation with CreateWindowEx() failed.", "Flagrant System Error", MB_ICONEXCLAMATION | MB_OK);
    return 0;
  }

  ShowWindow(w, show);
  UpdateWindow(w);

  while(GetMessage(&msg, NULL, 0, 0) > 0)
  {
    TranslateMessage(&msg);
    DispatchMessage(&msg);
  }

  return msg.wParam;
}
