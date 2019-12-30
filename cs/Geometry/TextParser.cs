//#define _DEBUG

/// TextParser.cs: simple text parser for String and TextReader objects
/// Version: 1.0, 26.10.2006
/// 
/// Copyright (C) 2006 Josef Kohout (University of West Bohemia)
/// All rights reserved.
///
/// http://herakles.zcu.cz
/// mailto:besoft@kiv.zcu.cz
///
/// This source code can be used, modified and redistributed
/// under the terms of the license agreement that is included
/// in the deployment package - it can be also downloaded from:
/// http://herakles.zcu.cz/research/license.txt
/// 
/// Warranties and Disclaimers:
/// THIS SOFTWARE IS PROVIDED "AS IS" WITHOUT WARRANTY OF ANY KIND
/// INCLUDING, BUT NOT LIMITED TO, WARRANTIES OF MERCHANTABILITY,
/// FITNESS FOR A PARTICULAR PURPOSE AND NON-INFRINGEMENT.
/// IN NO EVENT WILL AUTHORS BE LIABLE FOR ANY DIRECT,
/// INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY OR CONSEQUENTIAL DAMAGES,
/// INCLUDING DAMAGES FOR LOSS OF PROFITS, LOSS OR INACCURACY OF DATA,
/// INCURRED BY ANY PERSON FROM SUCH PERSON'S USAGE OF THIS SOFTWARE
/// EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGES.

using System;
using System.Collections.Generic;
using System.Text;
using System.Text.RegularExpressions;
using System.IO;
using System.Runtime.InteropServices;

namespace ZCU.IO
{
	class TextParser : IDisposable
	{
		#region Constants
		private const int DefaultBufferSize = 1024;
		private const int MinBufferSize = 32;
		private const int TokenBufferSize = 128;
		private const int MaxRegexInMap = 12;
		#endregion

		#region Attributes
		/// <summary>
		/// Underlaying TextReader object
		/// </summary>
		private TextReader base_reader = null;

		/// <summary>
		/// Regular expression for token delimiters
		/// </summary>
		private string delimiters = null;

		/// <summary>
		/// Buffer used to fetch data
		/// </summary>
		private char[] buffer = null;

		/// <summary>
		/// Current position in the buffer
		/// </summary>
		private int buffer_pos = 0;

		/// <summary>
		/// The valid length of buffer
		/// </summary>
		private int buffer_length = 0;

		/// <summary>
		/// Dictionary for fast location of previously used regular expressions
		/// </summary>
		private Dictionary<string, Regex> regexmap = null;
		#endregion

		#region Properties
		/// <summary>
		/// Gets the underlaying TextReader object.
		/// </summary>
		/// <value>The underlaying TextReader object.</value>
		public virtual TextReader BaseReader
		{
			get { return base_reader; }
		}

		/// <summary>
		/// Gets a value indicating whether the current position is at the end of stream.
		/// </summary>
		/// <value><c>true</c> if the current position is at the end of stream; otherwise, <c>false</c>.</value>
		public bool EndOfStream
		{
			get { return Peek() < 0; }
		}

		/// <summary>
		/// Gets or sets the default delimiters.
		/// </summary>
		/// <value>The regular expression for delimiters.</value>
		public string Delimiters
		{
			get { return delimiters; }
			set
			{
				regexmap.Remove(delimiters);    //remove previous Regex object
				delimiters = value;
			}
		}
		#endregion

		/// <summary>
		/// Initializes a new instance of the <see cref="TextParser"/> class.
		/// </summary>
		/// <param name="text">The text.</param>
		public TextParser(string text)
			: this(new StringReader(text))
		{
		}

		/// <summary>
		/// Initializes a new instance of the <see cref="TextParser"/> class.
		/// </summary>
		/// <param name="reader">The text reader, either StringReader or StreamReader.</param>
		public TextParser(TextReader reader)
			: this(reader, @"\s+")
		{

		}

		/// <summary>
		/// Initializes a new instance of the <see cref="TextParser"/> class.
		/// </summary>
		/// <param name="reader">The reader.</param>
		/// <param name="delimiters">The delimiters (in regular expression form).</param>
		public TextParser(TextReader reader, string delimiters)
			:
			this(reader, delimiters, DefaultBufferSize)
		{

		}

		/// <summary>
		/// Initializes a new instance of the <see cref="TextParser"/> class.
		/// </summary>
		/// <param name="reader">The reader.</param>
		/// <param name="delimiters">The delimiters (in regular expression form).</param>
		/// <param name="buffer_size">The size. for buffer</param>
		public TextParser(TextReader reader, string delimiters, int buffer_size)
		{
			if (reader == null)
				throw new ArgumentNullException("reader");

			if (delimiters == null)
				throw new ArgumentNullException("delimiters");

			if (buffer_size < MinBufferSize)
				throw new ArgumentException("Buffer must be at least " +
					MinBufferSize + " characters long", "buffer_size");

			this.base_reader = reader;
			this.delimiters = delimiters;
			this.regexmap = new Dictionary<string, Regex>();
			this.buffer = new char[buffer_size];
		}

		/// <summary>
		/// Closes the underlaying TextReader object and releases any system 
		/// resources associated with this TextParser.
		/// </summary>
		public void Close()
		{
			Dispose(true);
		}

		/// <summary>
		/// Reads the next character without changing the state of the reader or the 
		/// character source. Returns the next available character without actually 
		/// reading it from the input stream.
		/// </summary>
		/// <returns>The next character to be read, or -1 if no more characters are 
		/// available or the stream does not support seeking. </returns>
		public virtual int Peek()
		{
#if _DEBUG
            if (base_reader == null)
                throw new ObjectDisposedException("TextParser");
#endif
			if (buffer_pos >= buffer_length)
				return base_reader.Peek();

			return (int)buffer[buffer_pos];
		}

		/// <summary>
		/// Reads the next character from the input stream and advances the 
		/// character position by one character. 
		/// </summary>
		/// <returns>The next character from the input stream, or -1 if no more 
		/// characters are available. The default implementation returns -1. </returns>
		public virtual int Read()
		{
#if _DEBUG
            if (base_reader == null)
                throw new ObjectDisposedException("TextParser");
#endif
			if (buffer_pos >= buffer_length)
				return base_reader.Read();

			return (int)buffer[buffer_pos++];
		}

		/// <summary>
		/// Reads a maximum of count characters from the current stream and 
		/// writes the data to buffer, beginning at index. 
		/// </summary>
		/// <param name="dest_buffer">When this method returns, contains the specified 
		/// character array with the values between index and (index + count - 1) 
		/// replaced by the characters read from the current source.</param>
		/// <param name="index">The place in buffer at which to begin writing. </param>
		/// <param name="count">The maximum number of characters to read. 
		/// If the end of the stream is reached before count of characters is read 
		/// into buffer, the current method returns. </param>
		/// <returns>The number of characters that have been read. 
		/// The number will be less than or equal to count, depending on whether 
		/// the data is available within the stream. This method returns zero if 
		/// called when no more characters are left to read. </returns>
		public virtual int Read([In, Out] char[] dest_buffer, int index, int count)
		{
#if _DEBUG
            if (base_reader == null)
                throw new ObjectDisposedException("TextParser");
            if (dest_buffer == null)
                throw new ArgumentNullException("dest_buffer");
            if (index < 0)
                throw new ArgumentOutOfRangeException("index", "< 0");
            if (count < 0)
                throw new ArgumentOutOfRangeException("count", "< 0");
            // re-ordered to avoid possible integer overflow
            if (index > dest_buffer.Length - count)
                throw new ArgumentException("index + count > dest_buffer.Length");
#endif
			//read data from buffer
			int total_read = 0;
			int read = Math.Min(count, buffer_length - buffer_pos);
			if (read > 0)
			{
				Array.Copy(buffer, buffer_pos, dest_buffer, index, read);
				buffer_pos = buffer_length;
				total_read += read;
			} // if (read > 0)

			//read the remaining characters directly from the TextReader
			read = count - total_read;
			if (read > 0)
				total_read += base_reader.Read(dest_buffer, index + total_read, read);

			return total_read;
		}

		/// <summary>
		/// Reads a maximum of count characters from the current stream and writes 
		/// the data to buffer, beginning at index. 
		/// </summary>
		/// <param name="dest_buffer">When this method returns, this parameter contains 
		/// the specified character array with the values between index and 
		/// (index + count -1) replaced by the characters read from the current source. </param>
		/// <param name="index">The place in buffer at which to begin writing. </param>
		/// <param name="count">The maximum number of characters to read. </param>
		/// <returns>The number of characters that have been read. 
		/// The number will be less than or equal to count, depending on whether all 
		/// input characters have been read. </returns>
		/// <remarks>This is blocking version of Read</remarks>
		public virtual int ReadBlock([In, Out] char[] dest_buffer, int index, int count)
		{
#if _DEBUG
            if (base_reader == null)
                throw new ObjectDisposedException("TextParser");
            if (dest_buffer == null)
                throw new ArgumentNullException("dest_buffer");
            if (index < 0)
                throw new ArgumentOutOfRangeException("index", "< 0");
            if (count < 0)
                throw new ArgumentOutOfRangeException("count", "< 0");
            // re-ordered to avoid possible integer overflow
            if (index > dest_buffer.Length - count)
                throw new ArgumentException("index + count > dest_buffer.Length");
#endif
			//read data from buffer
			int total_read = 0;
			int read = Math.Min(count, buffer_length - buffer_pos);
			if (read > 0)
			{
				Array.Copy(buffer, buffer_pos, dest_buffer, index, read);
				buffer_pos = buffer_length;
				total_read += read;
			} // if (read > 0)

			//read the remaining characters directly from the TextReader
			read = count - total_read;
			if (read > 0)
				total_read += base_reader.ReadBlock(dest_buffer, index + total_read, read);

			return total_read;
		}

		/// <summary>
		/// Reads a line of characters from the current stream 
		/// and returns the data as a string. 
		/// </summary>
		/// <returns>The next line from the input stream, 
		/// or a null reference if all characters have been read. </returns>
		public virtual string ReadLine()
		{
#if _DEBUG
            if (base_reader == null)
                throw new ObjectDisposedException("TextParser");
#endif
			if (buffer_pos >= buffer_length)
				return base_reader.ReadLine();
			else
			{
				//read the buffer
				int start = buffer_pos;
				while (buffer_pos < buffer_length)
				{
					if (buffer[buffer_pos] == '\n')
					{
						buffer_pos++;
						return new String(buffer, start, buffer_pos - start);
					} // if (buffer[buffer_pos] == '\n')

					buffer_pos++;
				} // while (buffer_pos < buffer_length)

				//some data is in buffer
				string sPart1 = new String(buffer, start, buffer_pos - start);
				string sPart2 = base_reader.ReadLine();
				if (sPart2 == null)
					return sPart1;
				else
					return sPart1 + sPart2;

			} // if (buffer_pos ! = buffer_length)
		}

		/// <summary>
		/// Reads all characters from the current position to the end of the 
		/// underlaying TextReader object and returns them as one string. 
		/// </summary>
		/// <returns>A string containing all characters from the current position
		/// to the end of the TextReader object.</returns>
		public virtual string ReadToEnd()
		{
#if _DEBUG
            if (base_reader == null)
                throw new ObjectDisposedException("TextParser");
#endif
			if (buffer_pos >= buffer_length)
				return base_reader.ReadLine();
			else
			{
				//read the buffer
				//some data is in buffer
				string sPart1 = new String(buffer, buffer_pos, buffer_length - buffer_pos);
				buffer_pos = buffer_length; //move to end

				string sPart2 = base_reader.ReadLine();
				if (sPart2 == null)
					return sPart1;
				else
					return sPart1 + sPart2;
			}
		}

		/// <summary>
		/// Ensures that there is at least count characters in the buffer
		/// </summary>
		/// <returns>The number of characters available in the buffer</returns>
		/// <remarks>if the buffer cannot hold count characters it is enlarged.</remarks>
		private int FillBuffer(int count)
		{
			if (buffer_pos + count > buffer_length)
			{
				//some data must be fetched
				if (buffer_pos + count > buffer.Length)
				{
					//not enough space in the buffer
					int curLen = buffer_length - buffer_pos;
					char[] newBuffer = (curLen + count <= buffer.Length) ? buffer :
						new char[curLen + count];

					if (curLen > 0) //copy data if required
						Array.Copy(buffer, buffer_pos, newBuffer, 0, curLen);
					buffer_pos = 0;
					buffer_length = curLen;
					buffer = newBuffer;
				}

				//fill the rest of buffer
				buffer_length += base_reader.Read(buffer, buffer_length,
						buffer.Length - buffer_length);
			}

			return buffer_length - buffer_pos;
		}

		/// <summary>
		/// Reads a token matching the given pattern from the underlaying TextReader.
		/// </summary>
		/// <param name="pattern">The pattern.</param>
		/// <param name="throw_exception">if set to <c>true</c> FormatException is
		/// thrown if the pattern is not matched; otherwise empty string is returned.</param>
		/// <returns>
		/// The next token matching the given pattern from the
		/// underlaying TextReader, or a null reference if all characters
		/// have been read.
		/// </returns>
		public virtual string ReadToken(string pattern, bool throw_exception)
		{
#if _DEBUG
            if (base_reader == null)
                throw new ObjectDisposedException("TextParser");
#endif
			int charsAvail = FillBuffer(TokenBufferSize);
			if (charsAvail == 0)
				return null;    //EOF

			string s = new String(buffer, buffer_pos, charsAvail);

			Regex reg = GetRegex(pattern);
			Match match = reg.Match(s);
			if (!match.Success || match.Index != 0)
			{
				if (throw_exception)
					throw new FormatException("Next token doesn't match the pattern: " +
						pattern);
				else
					return String.Empty;
			}

			buffer_pos += match.Length;
			return s.Substring(0, match.Length);
		}

		/// <summary>
		/// Reads a token matching the given pattern from the underlaying TextReader.
		/// </summary>
		/// <param name="pattern">The pattern.</param>
		/// The next token matching the given pattern from the
		/// underlaying TextReader, or a null reference if all characters
		/// have been read.
		/// </returns>
		/// <remarks>Throws FormatException if the pattern is not matched.</remarks>
		public virtual string ReadToken(string pattern)
		{
			return ReadToken(pattern, true);
		}

		/// <summary>
		/// Reads the token from the underlaying TextReader object stopping at 
		/// the first delimiter.
		/// </summary>
		/// <returns>The token, the delimiters are not skipped</returns>
		public virtual string ReadToken()
		{
#if _DEBUG
            if (base_reader == null)
                throw new ObjectDisposedException("TextParser");
#endif
			int charsAvail = FillBuffer(TokenBufferSize);
			if (charsAvail == 0)
				return null;    //EOF

			string s = new String(buffer, buffer_pos, charsAvail);

			Regex reg = GetRegex(delimiters);
			Match match = reg.Match(s);
			if (match.Success)
			{
				buffer_pos += match.Index;
				return s.Substring(0, match.Index);
			}

			//not found in the tokensize => we need to read more data
			StringBuilder sb = new StringBuilder(s);
			buffer_pos += charsAvail;

			while ((charsAvail = FillBuffer(TokenBufferSize)) != 0)
			{
				sb.Append(buffer, buffer_pos, charsAvail);
				s = sb.ToString();

				match = reg.Match(s);
				if (match.Success)
				{
					buffer_pos += match.Index;
					return s.Substring(0, match.Index);
				}
			}

			//we are at EOF
			buffer_pos += charsAvail;
			return s;
		}

		/// <summary>
		/// Reads the delimiters.
		/// </summary>
		/// <returns>The read delimiters, null if we are at EOF</returns>
		public string ReadDelimiters()
		{
#if _DEBUG
            if (base_reader == null)
                throw new ObjectDisposedException("TextParser");
#endif
			string s = ReadToken(delimiters, false);
			if (s == null || s.Length == 0)
				return s;

			//there may be more delimiters
			StringBuilder sb = new StringBuilder(s);
			while ((s = ReadToken(delimiters, false)) != null)
			{
				if (s.Length == 0)
					break;

				sb.Append(s);
			}

			return sb.ToString();
		}

		/// <summary>
		/// Reads the value from the underlaying TextReader and advances the current 
		/// position of the stream in the TextReader.
		/// </summary>
		/// <returns>The string value.</returns>
		/// <remarks>Delimiters are skipped</remarks>
		private string ReadValue()
		{
			ReadDelimiters();   //skip delimiters
			string ret = ReadToken();
#if _DEBUG
            if (ret == null)
                throw new EndOfStreamException();
#endif

			ReadDelimiters();   //skip delimiters
			return ret.Trim().ToLower();
		}

		#region Value reading
		/// <summary>
		/// Reads a Boolean value from the underlaying TextReader and advances the current 
		/// position of the stream by the length of token. 
		/// </summary>
		/// <returns>true if the next token is nonzero; otherwise, false</returns>
		public virtual bool ReadBoolean()
		{
			string token = ReadValue();
			if (token == Boolean.TrueString.ToLower())
				return true;
			else if (token == Boolean.FalseString.ToLower())
				return false;

			// Not supported TrueFalseString 
			// => true if the byte is non-zero; otherwise false.
			return Convert.ToInt32(token) != 0;
		}

		/// <summary>
		/// Reads a Byte value from the underlaying TextReader and 
		/// advances the current position of the stream by the length of token. 
		/// </summary>
		/// <returns>The read byte.</returns>
		public virtual byte ReadByte()
		{
			return Convert.ToByte(ReadValue());
		}

		/// <summary>
		/// Reads a Char value from the underlaying TextReader and 
		/// advances the current position of the stream by the length of token. 
		/// </summary>
		/// <returns>The read char.</returns>
		public virtual char ReadChar()
		{
			int ch = Read();
			if (ch < 0)
				throw new EndOfStreamException();

			return ((char)ch);
		}

		/// <summary>
		/// Reads a Decimal value from the underlaying TextReader and 
		/// advances the current position of the stream by the length of token. 
		/// </summary>
		/// <returns>The read decimal.</returns>
		public virtual decimal ReadDecimal()
		{
			return Convert.ToDecimal(ReadValue());
		}

		/// <summary>
		/// Reads a Double value from the underlaying TextReader and 
		/// advances the current position of the stream by the length of token. 
		/// </summary>
		/// <returns>The read double.</returns>
		public virtual double ReadDouble()
		{
			return Convert.ToDouble(ReadValue());
		}

		/// <summary>
		/// Reads a Int16 value from the underlaying TextReader and 
		/// advances the current position of the stream by the length of token. 
		/// </summary>
		/// <returns>The read short.</returns>
		public virtual short ReadInt16()
		{
			return Convert.ToInt16(ReadValue());
		}

		/// <summary>
		/// Reads a Int32 value from the underlaying TextReader and 
		/// advances the current position of the stream by the length of token. 
		/// </summary>
		/// <returns>The read int.</returns>
		public virtual int ReadInt32()
		{
			return Convert.ToInt32(ReadValue());
		}

		/// <summary>
		/// Reads a Int64 value from the underlaying TextReader and 
		/// advances the current position of the stream by the length of token. 
		/// </summary>
		/// <returns>The read long.</returns>
		public virtual long ReadInt64()
		{
			return Convert.ToInt64(ReadValue());
		}

		/// <summary>
		/// Reads a Sbyte value from the underlaying TextReader and 
		/// advances the current position of the stream by the length of token. 
		/// </summary>
		/// <returns>The read sbyte.</returns>
		public virtual short ReadSByte()
		{
			return Convert.ToSByte(ReadValue());
		}

		/// <summary>
		/// Reads a Single value from the underlaying TextReader and 
		/// advances the current position of the stream by the length of token. 
		/// </summary>
		/// <returns>The read float.</returns>
		public virtual float ReadSingle()
		{
			return Convert.ToSingle(ReadValue());
		}

		/// <summary>
		/// Reads a UInt16 value from the underlaying TextReader and 
		/// advances the current position of the stream by the length of token. 
		/// </summary>
		/// <returns>The read ushort.</returns>
		public virtual ushort ReadUInt16()
		{
			return Convert.ToUInt16(ReadValue());
		}

		/// <summary>
		/// Reads a UInt32 value from the underlaying TextReader and 
		/// advances the current position of the stream by the length of token. 
		/// </summary>
		/// <returns>The read uint.</returns>
		public virtual uint ReadUInt32()
		{
			return Convert.ToUInt32(ReadValue());
		}

		/// <summary>
		/// Reads a UInt64 value from the underlaying TextReader and 
		/// advances the current position of the stream by the length of token. 
		/// </summary>
		/// <returns>The read ulong.</returns>
		public virtual ulong ReadUInt64()
		{
			return Convert.ToUInt64(ReadValue());
		}
		#endregion

		#region Formated value reading
		/// <summary>
		/// Reads a Boolean value from the underlaying TextReader and advances the current 
		/// position of the stream by the length of token. 
		/// </summary>
		/// <returns>true if the next token is nonzero; otherwise, false</returns>
		public virtual bool ReadBoolean(IFormatProvider provider)
		{
			return Convert.ToBoolean(ReadValue(), provider);
		}

		/// <summary>
		/// Reads a Byte value from the underlaying TextReader and 
		/// advances the current position of the stream by the length of token. 
		/// </summary>
		/// <returns>The read byte.</returns>
		public virtual byte ReadByte(IFormatProvider provider)
		{
			return Convert.ToByte(ReadValue(), provider);
		}

		/// <summary>
		/// Reads a Decimal value from the underlaying TextReader and 
		/// advances the current position of the stream by the length of token. 
		/// </summary>
		/// <returns>The read decimal.</returns>
		public virtual decimal ReadDecimal(IFormatProvider provider)
		{
			return Convert.ToDecimal(ReadValue(), provider);
		}

		/// <summary>
		/// Reads a Double value from the underlaying TextReader and 
		/// advances the current position of the stream by the length of token. 
		/// </summary>
		/// <returns>The read double.</returns>
		public virtual double ReadDouble(IFormatProvider provider)
		{
			return Convert.ToDouble(ReadValue(), provider);
		}

		/// <summary>
		/// Reads a Int16 value from the underlaying TextReader and 
		/// advances the current position of the stream by the length of token. 
		/// </summary>
		/// <returns>The read short.</returns>
		public virtual short ReadInt16(IFormatProvider provider)
		{
			return Convert.ToInt16(ReadValue(), provider);
		}

		/// <summary>
		/// Reads a Int32 value from the underlaying TextReader and 
		/// advances the current position of the stream by the length of token. 
		/// </summary>
		/// <returns>The read int.</returns>
		public virtual int ReadInt32(IFormatProvider provider)
		{
			return Convert.ToInt32(ReadValue(), provider);
		}

		/// <summary>
		/// Reads a Int64 value from the underlaying TextReader and 
		/// advances the current position of the stream by the length of token. 
		/// </summary>
		/// <returns>The read long.</returns>
		public virtual long ReadInt64(IFormatProvider provider)
		{
			return Convert.ToInt64(ReadValue(), provider);
		}

		/// <summary>
		/// Reads a Sbyte value from the underlaying TextReader and 
		/// advances the current position of the stream by the length of token. 
		/// </summary>
		/// <returns>The read sbyte.</returns>
		public virtual short ReadSByte(IFormatProvider provider)
		{
			return Convert.ToSByte(ReadValue(), provider);
		}

		/// <summary>
		/// Reads a Single value from the underlaying TextReader and 
		/// advances the current position of the stream by the length of token. 
		/// </summary>
		/// <returns>The read float.</returns>
		public virtual float ReadSingle(IFormatProvider provider)
		{
			return Convert.ToSingle(ReadValue(), provider);
		}

		/// <summary>
		/// Reads a UInt16 value from the underlaying TextReader and 
		/// advances the current position of the stream by the length of token. 
		/// </summary>
		/// <returns>The read ushort.</returns>
		public virtual ushort ReadUInt16(IFormatProvider provider)
		{
			return Convert.ToUInt16(ReadValue(), provider);
		}

		/// <summary>
		/// Reads a UInt32 value from the underlaying TextReader and 
		/// advances the current position of the stream by the length of token. 
		/// </summary>
		/// <returns>The read uint.</returns>
		public virtual uint ReadUInt32(IFormatProvider provider)
		{
			return Convert.ToUInt32(ReadValue(), provider);
		}

		/// <summary>
		/// Reads a UInt64 value from the underlaying TextReader and 
		/// advances the current position of the stream by the length of token. 
		/// </summary>
		/// <returns>The read ulong.</returns>
		public virtual ulong ReadUInt64(IFormatProvider provider)
		{
			return Convert.ToUInt64(ReadValue(), provider);
		}
		#endregion

		#region Radix value readin
		/// <summary>
		/// Reads a Byte value from the underlaying TextReader and 
		/// advances the current position of the stream by the length of token. 
		/// </summary>
		/// <param name="fromBase">2,8,10 or 16 or 0 to be autodetected</param>
		/// <returns>The read byte.</returns>
		public virtual byte ReadByte(int fromBase)
		{
			string value = ReadValue();
			if (fromBase == 0)
				fromBase = DetectIntegerBase(ref value);

			return Convert.ToByte(value, fromBase);
		}

		/// <summary>
		/// Reads a Int16 value from the underlaying TextReader and 
		/// advances the current position of the stream by the length of token. 
		/// </summary>
		/// <param name="fromBase">2,8,10 or 16 or 0 to be autodetected</param>
		/// <returns>The read short.</returns>
		public virtual short ReadInt16(int fromBase)
		{
			string value = ReadValue();
			if (fromBase == 0)
				fromBase = DetectIntegerBase(ref value);

			return Convert.ToInt16(value, fromBase);
		}

		/// <summary>
		/// Reads a Int32 value from the underlaying TextReader and 
		/// advances the current position of the stream by the length of token. 
		/// </summary>
		/// <param name="fromBase">2,8,10 or 16 or 0 to be autodetected</param>
		/// <returns>The read int.</returns>
		public virtual int ReadInt32(int fromBase)
		{
			string value = ReadValue();
			if (fromBase == 0)
				fromBase = DetectIntegerBase(ref value);

			return Convert.ToInt32(value, fromBase);
		}

		/// <summary>
		/// Reads a Int64 value from the underlaying TextReader and 
		/// advances the current position of the stream by the length of token. 
		/// </summary>
		/// <param name="fromBase">2,8,10 or 16 or 0 to be autodetected</param>
		/// <returns>The read long.</returns>
		public virtual long ReadInt64(int fromBase)
		{
			string value = ReadValue();
			if (fromBase == 0)
				fromBase = DetectIntegerBase(ref value);

			return Convert.ToInt64(value, fromBase);
		}

		/// <summary>
		/// Reads a Sbyte value from the underlaying TextReader and 
		/// advances the current position of the stream by the length of token. 
		/// </summary>
		/// <param name="fromBase">2,8,10 or 16 or 0 to be autodetected</param>
		/// <returns>The read sbyte.</returns>
		public virtual short ReadSByte(int fromBase)
		{
			string value = ReadValue();
			if (fromBase == 0)
				fromBase = DetectIntegerBase(ref value);

			return Convert.ToSByte(value, fromBase);
		}

		/// <summary>
		/// Reads a UInt16 value from the underlaying TextReader and 
		/// advances the current position of the stream by the length of token. 
		/// </summary>
		/// <param name="fromBase">2,8,10 or 16 or 0 to be autodetected</param>
		/// <returns>The read ushort.</returns>
		public virtual ushort ReadUInt16(int fromBase)
		{
			string value = ReadValue();
			if (fromBase == 0)
				fromBase = DetectIntegerBase(ref value);

			return Convert.ToUInt16(ReadValue(), fromBase);
		}

		/// <summary>
		/// Reads a UInt32 value from the underlaying TextReader and 
		/// advances the current position of the stream by the length of token. 
		/// </summary>
		/// <param name="fromBase">2,8,10 or 16 or 0 to be autodetected</param>
		/// <returns>The read uint.</returns>
		public virtual uint ReadUInt32(int fromBase)
		{
			string value = ReadValue();
			if (fromBase == 0)
				fromBase = DetectIntegerBase(ref value);

			return Convert.ToUInt32(ReadValue(), fromBase);
		}

		/// <summary>
		/// Reads a UInt64 value from the underlaying TextReader and 
		/// advances the current position of the stream by the length of token. 
		/// </summary>\n<param name="fromBase">2,8,10 or 16 or 0 to be autodetected</param>
		/// <returns>The read ulong.</returns>
		public virtual ulong ReadUInt64(int fromBase)
		{
			string value = ReadValue();
			if (fromBase == 0)
				fromBase = DetectIntegerBase(ref value);

			return Convert.ToUInt64(ReadValue(), fromBase);
		}

		/// <summary>
		/// Detects the base for the given integer in string.
		/// </summary>
		/// <param name="value">The string value (suffixes and prefixes are trimmed).</param>
		/// <returns>The detected base, 0 if an error occurs</returns>
		/// <remarks>
		/// If the string is prefixed by '0X' or '0x' the rest of the string 
		/// is interpreted as a hexadecimal integer; otherwise 
		/// if the string is suffixed by 'D' or 'd', the string 
		/// is interpreted as a decimal integer; otherwise
		/// if the string is suffixed by 'B' or 'b', the string 
		/// is interpreted as a binary integer; otherwise
		/// if the string is prefixed by '0', the string 
		/// is interpreted as a octal integer; otherwise the string 
		/// is interpreted as a decimal integer </remarks>
		public static int DetectIntegerBase(ref string value)
		{
			char prefix = value[0];

			if (prefix == '0' && value.Length >= 2 &&
				(value[1] == 'x' || value[1] == 'X'))
			{
				value = value.Substring(2);
				return 16;
			}

			int ret = 0;
			char suffix = Char.ToUpper(value[value.Length - 1]);
			switch (suffix)
			{
				case 'B': ret = 2; break;
				case 'O': ret = 8; break;
				case 'D': ret = 10; break;
				case 'H': ret = 16; break;
			}

			if (ret != 0)
			{
				value = value.Substring(0, value.Length - 1);
				return ret;
			}

			if (prefix == '0')
				return 8;
			else
				return 10;
		}
		#endregion

		#region C/C++ Scanf
		/// <summary>
		/// Reads formatted data from the underlaying TextReader.
		/// </summary>
		/// <seealso cref="C/C++ scanf CRT function"/>
		/// <param name="format">The format string.</param>
		/// <param name="values">The array of values to be retrieved.</param>
		/// <returns>The number of fields converted and assigned.
		/// A return value of 0 indicates that no fields were assigned. 
		/// The return value is -1 for an error or if the end-of-file character 
		/// or the end-of-string character is encountered in the first attempt 
		/// to read a character.</returns>
		/// <remarks>
		/// While the array values must be allocated, its values may not be and
		/// they are constructed according to the format string (if it is possible,
		/// otherwise an error occurs).
		/// 
		/// Format string may contain C/C++ format specifiers (%d, ...) or
		/// .NET format specifier {index}
		/// 
		/// If the next value for the given .NET specifier is to be fetch,
		/// the appropriate value in the array values must be already instantiated
		/// and its type determines how the next value will be retrieved;
		/// otherwise an error occurs.
		/// 
		/// If the next value for the given C/C++ specifier is to be fetch and
		/// the appropriate value in the array values is already instantiated,
		/// then the specifier is compared to the type of the value. If they do not
		/// match, an error occurs. Possible matches are as follows:
		/// %d ... any integer value (Byte, Sbyte, Int16, UInt16, Int32, UInt32, Int64 and UInt64)
		/// %i64 ... Int64 only
		/// %ui64 ... UInt64 only
		/// %c ... Char
		/// %s ... String (just one word)
		/// %f ... any floating value (Single, Double)
		/// %lf ... Double only
		/// 
		/// If the next value for the given C/C++ specifier is to be fetch and
		/// the appropriate value in the array values is not already instantiated,
		/// then the specifier denotes how the next value will be retrieved:
		/// 
		/// %d ... Int32
		/// %i64 ... Int64
		/// %ui64 ... UInt64
		/// %c ... Char
		/// %s ... String (just one word)
		/// %f ... Single
		/// %lf ... Double
		/// 
		/// Call: CANNOT BE CALLED Scanf(format, a, b, c) like from C/C++ 
		/// but: object[] ret = new object[n]; Scanf(format, ret); a = (double)ret[0]; ...
		///</remarks>
		public int Scanf(string format, params object[] values)
		{
#if _DEBUG
            if (format == null)
                throw new ArgumentNullException("format");
#endif
			return Scanf(ConstructFormatList(format), values);
		}

		/// <summary>
		/// Constructs the format list from the given format string.
		/// </summary>
		/// <param name="format">The format string.</param>
		/// <returns>The appropriate format list</returns>
		public static List<string> ConstructFormatList(string format)
		{
			#region Format string optimization
			//modify the format string in order to simplify separators processingg
			StringBuilder sb = new StringBuilder(format);

			int i = 0;
			while (i < sb.Length)
			{
				char c = sb[i];
				if (c == '\r' || c == '\n' || c == '\t')
					sb[i] = c = ' ';

				i++;    //move next

				if (c == ' ')
				{
					int spaces = 0;
					while (i < sb.Length)
					{
						c = sb[i];      //read next char
						if (c != ' ' && c != '\r' &&
							c != '\n' && c != '\t')
							break;
						spaces++;
						i++;
					}

					if (spaces > 0)
					{
						//remove spaces
						i -= spaces;
						sb.Remove(i, spaces);
					}
				}
			}

			format = sb.ToString();
			#endregion

			#region Format string segmentation
			//any separator in the format is now replaced by ' ' and we treat
			//sequences of seperators as one
			List<string> parts = new List<string>();

			i = 0;
			int iPos = 0;   //last position
			while (i < sb.Length)
			{
				int iStart;
				char c = sb[i]; //get char
				switch (c)
				{
					case '%':
						//C/C++ format specifier
						iStart = i;
						if (++i == sb.Length)
							break;                  //end of the format string

						if ((c = sb[i]) != '%')
							i++;
						else
						{
							sb.Remove(i, 1);
							format = sb.ToString();
							continue;               //%% case -> proceed with processing
						}

						//it could be format specifier starting at i - 1
						if (c == 'u' || c == 'l')
						{
							//fetch another character
							if (i == sb.Length)
								break;   //wrong format string

							c = sb[i++];
						}

						if (c == 'i')
						{
							//fetch two more characters (64)
							if (i + 1 >= sb.Length)
								break;   //wrong format string

							i += 2;
						}

						string spec = format.Substring(iStart, i - iStart);
						if (spec == "%d" || spec == "%f" || spec == "%lf" ||
							spec == "%s" || spec == "%c" || spec == "%i64" || spec == "%ui64")
						{
							//separator found
							if (iStart > iPos)
								parts.Add(format.Substring(iPos, iStart - iPos));

							parts.Add('{' + spec + '}');
							iPos = i;
						}
						break;

					case '{':
						//.NET format specifier
						//read the format string until '}' is found
						iStart = i++;
						while (i < sb.Length && sb[i] != '}')
							i++;

						if (i == format.Length)
							break;       //wrong format string

						//sb contains the index
						//separator found
						if (iStart > iPos)
							parts.Add(format.Substring(iPos, iStart - iPos));

						parts.Add(format.Substring(iStart, i - iStart + 1));
						iPos = ++i;
						break;

					default: i++;
						break;
				}
			}

			if (iPos < format.Length)
				parts.Add(format.Substring(iPos));
			#endregion

			return parts;
		}

		/// Reads formatted data from the underlaying TextReader.
		/// </summary>
		/// <seealso cref="Scanf(string, params object[])"/>
		/// <param name="format">The format string (C/C++ format only).</param>
		/// <returns>The array of retrieved objects</returns>
		/// <remarks>Note: this method is slower than Scanf(format, values).</remarks>
		public object[] Scanf(string format)
		{
#if _DEBUG
            if (format == null)
                throw new ArgumentNullException("format");
#endif
			return Scanf(ConstructFormatList(format));
		}

		/// Reads formatted data from the underlaying TextReader.
		/// </summary>
		/// <seealso cref="Scanf(string, params object[])"/>
		/// <param name="format">The format string (C/C++ format only).</param>
		/// <returns>The array of retrieved objects</returns>
		/// <remarks>Note: this method is slower than Scanf(formatList, values)
		/// but faster than Scanf(format).</remarks>
		public object[] Scanf(List<string> formatList)
		{
#if _DEBUG
            if (formatList == null)
                throw new ArgumentNullException("formatList");
#endif
			int nLength = 0;
			foreach (string s in formatList)
			{
				if (s[0] == '{')
					nLength++;
			}

			object[] buffer = new object[nLength];
			nLength = Scanf(formatList, buffer);
			if (nLength < 0)
				throw new EndOfStreamException();

			if (nLength == buffer.Length)
				return buffer;
			else
			{
				object[] ret = new object[nLength];
				Array.Copy(buffer, ret, nLength);
				return ret;
			}
		}


		/// <summary>
		/// Reads formatted data from the underlaying TextReader.
		/// </summary>
		/// <param name="formatList">The format list, obtained from ConstructFormatList.</param>
		/// <param name="values">The array of values to be retrieved.</param>
		/// <returns>The number of fields converted and assigned.
		/// A return value of 0 indicates that no fields were assigned. 
		/// The return value is -1 for an error or if the end-of-file character 
		/// or the end-of-string character is encountered in the first attempt 
		/// to read a character.</returns>
		/// <remarks>This method should be used instead of Scanf method with
		/// the format string whenever, the same format string is used repeatedly,
		/// e.g., in cycles, to improve the efficiency</remarks>
		/// <seealso cref="public int Scanf(string format, params object[] values)"/>
		public int Scanf(List<string> formatList, params object[] values)
		{
#if _DEBUG
            if (formatList == null)
                throw new ArgumentNullException("formatList");

            if (values == null)
                throw new ArgumentNullException("values");
#endif
			if (EndOfStream)
				return -1;

			#region Main processing
			//the format string has been processed
			ReadToken(@"\s*");

			int processed = 0;
			try
			{
				for (int i = 0; i < formatList.Count; i++)
				{
					string s = formatList[i];
					if (s[0] != '{')
					{
						s = Regex.Escape(s).Replace("\\ ", "\\s+");
						s = ReadToken(s, false);
						if (s == null || s.Length == 0)
							return processed;   //wrong input
					}
					else
					{
						int index;
						string type = String.Empty;
						s = s.Substring(1, s.Length - 2);
						if (s[0] != '%')
						{
							index = Convert.ToInt32(s);
							s = String.Empty;

							if (values[index] == null)
								throw new ArgumentException
									("Unable to detect type for values[" + index + "].");

							type = values[index].GetType().FullName;
						}
						else
						{
							index = processed;
							if (values[index] != null)
								type = values[index].GetType().FullName;
							else
							{
								switch (s)
								{
									case "%d": type = "System.Int32"; break;
									case "%c": type = "System.Char"; break;
									case "%s": type = "System.String"; break;
									case "%f": type = "System.Single"; break;
									case "%lf": type = "System.Double"; break;
									case "%i64": type = "System.Int64"; break;
									case "%ui64": type = "System.UInt64"; break;
								}
							}
						} //end if (s[0] != '%')

						//now we now the required type, we need to fetch it
						bool bFetched = false;
						if (type == "System.Char")
						{
							if (s.Length == 0 || s == "%c")
							{ values[index] = ReadChar(); bFetched = true; }
						}
						else
						{
							string old_delim = delimiters;
							if (i + 1 == formatList.Count)
								delimiters = @"\s+";
							else
							{
								delimiters = formatList[i + 1];
								delimiters = Regex.Escape(delimiters).Replace("\\ ", "\\s+");
							}

							string value = ReadToken();
							if (value == null || value.Length == 0)
								return processed;

							delimiters = old_delim;

							//parse read value
							#region Value parsing
							int radix;
							switch (type)
							{
								case "System.Byte":
									if (s.Length == 0 || s == "%d")
									{
										radix = DetectIntegerBase(ref value);
										values[index] = Convert.ToByte(value, radix);
										bFetched = true;
									}
									break;
								case "System.Int16":
									if (s.Length == 0 || s == "%d")
									{
										radix = DetectIntegerBase(ref value);
										values[index] = Convert.ToInt16(value, radix);
										bFetched = true;
									}
									break;
								case "System.Int32":
									if (s.Length == 0 || s == "%d")
									{
										radix = DetectIntegerBase(ref value);
										values[index] = Convert.ToInt32(value, radix);
										bFetched = true;
									}
									break;
								case "System.Int64":
									if (s.Length == 0 || s == "%d" || s == "%i64")
									{
										radix = DetectIntegerBase(ref value);
										values[index] = Convert.ToInt64(value, radix);
										bFetched = true;
									}
									break;
								case "System.Single":
									if (s.Length == 0 || s == "%f")
									{
										values[index] = Convert.ToSingle(value);
										bFetched = true;
									}
									break;
								case "System.String":
									if (s.Length == 0 || s == "%s")
									{
										values[index] = value;
										bFetched = true;
									}
									break;
								case "System.SByte":
									if (s.Length == 0 || s == "%d")
									{
										radix = DetectIntegerBase(ref value);
										values[index] = Convert.ToSByte(value, radix);
										bFetched = true;
									}
									break;
								case "System.UInt16":
									if (s.Length == 0 || s == "%d")
									{
										radix = DetectIntegerBase(ref value);
										values[index] = Convert.ToUInt16(value, radix);
										bFetched = true;
									}
									break;
								case "System.UInt32":
									if (s.Length == 0 || s == "%d")
									{
										radix = DetectIntegerBase(ref value);
										values[index] = Convert.ToUInt32(value, radix);
										bFetched = true;
									}
									break;
								case "System.UInt64":
									if (s.Length == 0 || s == "%d" || s == "%ui64")
									{
										radix = DetectIntegerBase(ref value);
										values[index] = Convert.ToUInt64(value, radix);
										bFetched = true;
									}
									break;
								case "System.Double":
									if (s.Length == 0 || s == "%f" || s == "%lf")
									{
										values[index] = Convert.ToDouble(value);
										bFetched = true;
									}
									break;
								case "System.Decimal":
									if (s.Length == 0 || s == "%f")
									{
										values[index] = Convert.ToDecimal(value);
										bFetched = true;
									}
									break;
							} //end switch 
							#endregion

						}//end if (else) if (type == "System.Char")

						if (!bFetched)
							throw new ArgumentException("Incompatible types");
						processed++;
					} //end if (else) if (s[0] != '{')
				} //end for
			}
			catch (FormatException e)
			{
				System.Diagnostics.Debug.WriteLine(e.ToString());
			}
			#endregion
			return processed;
		}
		#endregion

		/// <summary>
		/// Gets the Regex object for the given pattern.
		/// </summary>
		/// <param name="pattern">The pattern.</param>
		/// <returns>The Regex object.</returns>
		private Regex GetRegex(string pattern)
		{
			Regex reg;
			if (!regexmap.TryGetValue(pattern, out reg))
			{
				reg = new Regex(pattern, RegexOptions.Compiled);
				if (regexmap.Count == MaxRegexInMap)
				{
					string key = regexmap.GetEnumerator().Current.Key;
					regexmap.Remove(key);
				} // if (regexmap.Count == MaxRegexInMap)

				regexmap.Add(pattern, reg);
			} // if (!regexmap.TryGetValue(pattern, out reg))

			return reg;
		} // if (!regexmap.TryGetValue(pattern, out reg))

		#region IDisposable Members

		/// <summary>
		/// Performs application-defined tasks associated with freeing, releasing, or resetting unmanaged resources.
		/// </summary>
		void IDisposable.Dispose()
		{
			Dispose(true);
		}

		/// <summary>
		/// Releases the unmanaged resources used by the TextParser and optionally 
		/// releases the managed resources. 
		/// </summary>
		/// <param name="disposing">if set to <c>true</c> both managed and unmanaged resources are releaser; 
		/// <c>false</c> only unmanaged resources are released.</param>
		protected virtual void Dispose(bool disposing)
		{
			if (disposing && base_reader != null)
				base_reader.Close();

			base_reader = null;
		}

		#endregion
	}
}
